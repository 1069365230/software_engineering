package com.ase.recommenderservice.model.dto;

import com.ase.recommenderservice.model.Attendee;
import com.ase.recommenderservice.model.Event;
import com.ase.recommenderservice.model.EventCategory;
import com.ase.recommenderservice.model.dto.incoming.AttendeeDTO;
import com.ase.recommenderservice.model.dto.incoming.EventDTO;
import com.ase.recommenderservice.model.dto.outgoing.RecommendationDTO;
import org.springframework.stereotype.Component;

/**
 * Converts to/from DTOs to/from the internal data structure
 */
@Component
public class DTOConverter {

    public RecommendationDTO convertToRecommendationDTO(Event event, double relevanceScore) {
        RecommendationDTO recommendationDTO = new RecommendationDTO(event.getId(), event.getName(), event.getType().getName(),
                event.getCity(), event.getCountry(), event.getStartDate(), event.getEndDate(), relevanceScore);
        return recommendationDTO;
    }

    public Attendee convertToAttendee(AttendeeDTO attendeeDTO) {
        Attendee attendee = new Attendee();
        attendee.setId(attendeeDTO.getId());
        attendee.setEmail(attendeeDTO.getEmail());
        attendee.setCountry(attendeeDTO.getCountryCode());
        attendee.setCity(attendeeDTO.getHometown());
        return attendee;
    }

    public Event convertToEvent(EventDTO eventDTO) {
        Event event = new Event();
        event.setId(eventDTO.getId());
        event.setType(new EventCategory(eventDTO.getType()));
        event.setName(eventDTO.getName());
        event.setVacancies(eventDTO.getMaxCapacity());
        event.setCountry(eventDTO.getCountry());
        event.setCity(eventDTO.getCity());
        event.setStartDate(eventDTO.getStartDate());
        event.setEndDate(eventDTO.getEndDate());
        return event;
    }


}
