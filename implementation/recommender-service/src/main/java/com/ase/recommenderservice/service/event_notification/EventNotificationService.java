package com.ase.recommenderservice.service.event_notification;

import com.ase.recommenderservice.model.Attendee;
import com.ase.recommenderservice.model.EventCategory;
import com.ase.recommenderservice.model.dto.incoming.EventDTO;
import com.ase.recommenderservice.repository.AttendeeRepository;
import jakarta.transaction.Transactional;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventNotificationService {
    private AttendeeRepository attendeeRepository;
    private EmailSender emailSender;

    public EventNotificationService(EmailSender emailSender, AttendeeRepository attendeeRepository) {
        this.emailSender = emailSender;
        this.attendeeRepository = attendeeRepository;
    }

    /**
     * Determine all attendees to notify which are located in the same city as the event
     * and more than 30% of previously attended events had the same category
     *
     * @param event The newly created event
     */
    @EventListener
    @Async
    @Transactional
    public void determineInterestedAttendees(EventDTO event) {
        List<Attendee> attendeeCandidates = attendeeRepository.findAttendeeCandidates(event.getCity());
        EventCategory eventCategory = new EventCategory(event.getType());

        double threshold = 0.3;
        List<String> recipientEmails = attendeeCandidates.stream()
                .filter(attendee -> {
                    double interestSum = attendee.getPrimaryInterests().values().stream().reduce(0.0, Double::sum);
                    double eventInterest = attendee.getPrimaryInterests().getOrDefault(eventCategory, 0.0);
                    return eventInterest > interestSum * threshold; // event-category must account to > 30% of all attendee interests
                })
                .map(attendee -> attendee.getEmail())
                .collect(Collectors.toList());

        if (!recipientEmails.isEmpty())
            emailSender.sendEmails(recipientEmails, "New Event Recommendation!", event.getStringRepresentation());
    }
}
