package com.ase.recommenderservice.service.recommendation;

import com.ase.recommenderservice.constants.MessageConstants;
import com.ase.recommenderservice.exceptions.ResourceNotFoundException;
import com.ase.recommenderservice.model.Attendee;
import com.ase.recommenderservice.model.Event;
import com.ase.recommenderservice.model.EventCategory;
import com.ase.recommenderservice.model.dto.DTOConverter;
import com.ase.recommenderservice.model.dto.incoming.*;
import com.ase.recommenderservice.model.dto.outgoing.RecommendationDTO;
import com.ase.recommenderservice.repository.AttendeeRepository;
import com.ase.recommenderservice.repository.EventRepository;
import com.ase.recommenderservice.service.recommendation.strategy.GeneralRecommendationGenerator;
import com.ase.recommenderservice.service.recommendation.strategy.PersonalRecommendationGenerator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class RecommendationManagement {
    private EventRepository eventRepository;
    private AttendeeRepository attendeeRepository;
    private RecommenderService recommenderService;
    private DTOConverter dtoConverter;

    @Autowired
    public RecommendationManagement(EventRepository eventRepository, AttendeeRepository attendeeRepository,
                                    RecommenderService recommenderService, DTOConverter dtoConverter) {
        this.eventRepository = eventRepository;
        this.attendeeRepository = attendeeRepository;
        this.recommenderService = recommenderService;
        this.dtoConverter = dtoConverter;
    }

    /**
     * Process and create an Event based on the received event information
     *
     * @param eventDTO The received event information
     */
    @EventListener
    @Async
    public void processEventEntry(EventDTO eventDTO) {
        Event event = dtoConverter.convertToEvent(eventDTO);
        eventRepository.save(event);
    }

    /**
     * Process and EventUpdateDTO and update the Event in the database
     *
     * @param eventUpdateDTO The received event update information with new start-/endDate
     */
    @EventListener
    @Async
    public void processEventUpdate(EventUpdateDTO eventUpdateDTO) {
        Optional<Event> updatedEvent = eventRepository.findById(eventUpdateDTO.getEventId());
        if (updatedEvent.isPresent()) {
            updatedEvent.get().setStartDate(eventUpdateDTO.getNewStartDate());
            updatedEvent.get().setEndDate(eventUpdateDTO.getNewEndDate());
            eventRepository.saveAndFlush(updatedEvent.get());
        }
    }


    /**
     * Process and create an Attendee based on the registration information
     *
     * @param attendeeDTO The received attendee information
     */
    @EventListener
    @Async
    public void processAttendeeRegistration(AttendeeDTO attendeeDTO) {
        Attendee attendee = dtoConverter.convertToAttendee(attendeeDTO);
        attendeeRepository.save(attendee);
    }

    /**
     * Process an event-booking or cancellation and update the attendee's interests
     *
     * @param eventBooking The received eventBooking
     */
    @EventListener
    @Async
    public void processEventBooking(EventBookingDTO eventBooking) {
        Attendee attendee = retrieveAttendeeIfExists(eventBooking.getAttendeeId());
        Event attendingEvent = retrieveEventIfExists(eventBooking.getEventId());

        if (attendee.getAttendingEvents().contains(attendingEvent))
            return;
        if (eventBooking.isActive())
            attendee.getAttendingEvents().add(attendingEvent);
        else
            attendee.getAttendingEvents().remove(attendingEvent);

        updateAttendeeInterests(attendee, attendingEvent.getType(), eventBooking);
        eventRepository.updateEventVacancy(eventBooking.getEventId());
        eventRepository.save(attendingEvent);
        attendeeRepository.save(attendee);
    }

    /**
     * Process a bookmark / unbookmark event and update the attendee's interests
     *
     * @param bookmarkDTO The received bookmark-event
     */
    @EventListener
    @Async
    public void processBookmarkEntry(BookmarkDTO bookmarkDTO) {
        Attendee attendee = retrieveAttendeeIfExists(bookmarkDTO.getAttendeeId());
        Event attendingEvent = retrieveEventIfExists(bookmarkDTO.getEventId());

        if (bookmarkDTO.isActive())
            attendee.getBookmarkedEvents().add(attendingEvent);
        else
            attendee.getBookmarkedEvents().remove(attendingEvent);

        updateAttendeeInterests(attendee, attendingEvent.getType(), bookmarkDTO);
        attendeeRepository.save(attendee);
    }

    /**
     * Generate and return personalized recommendations for an Attendee
     *
     * @param attendeeId The attendee who made the request
     * @return A list of event recommendations {@link RecommendationDTO}
     */
    public List<RecommendationDTO> processPersonalRecommendationRequest(Long attendeeId) {
        Attendee attendee = retrieveAttendeeIfExists(attendeeId);
        List<RecommendationDTO> recommendations;
        // Choose strategy
        if (attendee.getPrimaryInterests().isEmpty())
            recommendations = recommenderService.generateRecommendations(new GeneralRecommendationGenerator(), attendee);
        else
            recommendations = recommenderService.generateRecommendations(new PersonalRecommendationGenerator(), attendee);

        return recommendations;
    }

    /**
     * Update the attendee interest-scores based on a bookmark or attendance event
     *
     * @param attendee         The attendee for whom to update the interests-scores
     * @param categoryKey      The EventCategory of the interacted Event (bookmarking or attending)
     * @param eventInteraction An EventInteraction which either represents a Bookmark (score += 0.5) or
     *                         and Attendance (score += 1)
     */
    private void updateAttendeeInterests(Attendee attendee, EventCategory categoryKey, EventInteraction eventInteraction) {
        Map<EventCategory, Double> attendeeInterests = attendee.getPrimaryInterests();
        double interestScore = 1.0;

        // Bookmarks only account for half to the interest score
        if (eventInteraction instanceof BookmarkDTO)
            interestScore = 0.5;

        if (!eventInteraction.isActive())
            interestScore *= -1;

        // Add the key-value pair if it does not exist, otherwise sum the interestScore to the current value
        attendeeInterests.merge(categoryKey, interestScore, Double::sum);

        // If the interest score is equal to 0, remove it
        if (attendeeInterests.get(categoryKey) == 0.0)
            attendeeInterests.remove(categoryKey);
    }

    private Attendee retrieveAttendeeIfExists(Long attendeeId) {
        Attendee attendee = attendeeRepository.findById(attendeeId).orElseThrow(() -> new ResourceNotFoundException(
                MessageFormat.format(MessageConstants.ENTITY_NOT_FOUND, "Attendee", attendeeId)));
        return attendee;
    }

    private Event retrieveEventIfExists(Long eventId) {
        Event attendingEvent = eventRepository.findById(eventId).orElseThrow(() -> new ResourceNotFoundException(
                MessageFormat.format(MessageConstants.ENTITY_NOT_FOUND, "Event", eventId)));
        return attendingEvent;
    }
}
