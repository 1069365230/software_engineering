package com.ase.attendanceservice.model.dto;

import com.ase.attendanceservice.model.Attendee;
import com.ase.attendanceservice.model.Event;
import com.ase.attendanceservice.model.dto.incoming.AttendeeDTO;
import com.ase.attendanceservice.model.dto.incoming.EventDTO;
import org.springframework.stereotype.Component;

@Component
public class DTOConverter {

    /**
     * Convert a received {@link EventDTO} to the internal {@link Event}}
     *
     * @param eventDTO The received eventDTO to convert
     * @return The converted Event
     */
    public Event convertToEvent(EventDTO eventDTO) {
        Event event = new Event();
        event.setId(eventDTO.getId());
        event.setOrganizerId(eventDTO.getOrganizerId());
        event.setName(eventDTO.getName());
        event.setMaxCapacity(eventDTO.getMaxCapacity());
        event.setVacancies(eventDTO.getMaxCapacity());
        event.setStartDate(eventDTO.getStartDate());
        event.setEndDate(eventDTO.getEndDate());
        return event;
    }

    /**
     * Convert a received {@link AttendeeDTO} to the internal {@link Attendee}}
     *
     * @param attendeeDTO The received attendeeDTO to convert
     * @return The converted Attendee
     */
    public Attendee convertToAttendee(AttendeeDTO attendeeDTO) {
        Attendee attendee = new Attendee();
        attendee.setId(attendeeDTO.getId());
        attendee.setEmail(attendeeDTO.getEmail());
        attendee.setFirstname(attendeeDTO.getForename());
        attendee.setLastname(attendeeDTO.getSurname());
        return attendee;
    }

}
