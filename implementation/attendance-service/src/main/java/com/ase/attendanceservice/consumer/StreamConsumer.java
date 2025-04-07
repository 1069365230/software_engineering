package com.ase.attendanceservice.consumer;

import com.ase.attendanceservice.model.Attendee;
import com.ase.attendanceservice.model.Event;
import com.ase.attendanceservice.model.dto.DTOConverter;
import com.ase.attendanceservice.model.dto.incoming.AttendeeDTO;
import com.ase.attendanceservice.model.dto.incoming.EventDTO;
import com.ase.attendanceservice.model.dto.incoming.EventUpdateDTO;
import com.ase.attendanceservice.repository.AttendeeRepository;
import com.ase.attendanceservice.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Consumer;

@Component
public class StreamConsumer {
    private DTOConverter dtoConverter;
    private AttendeeRepository attendeeRepository;
    private EventRepository eventRepository;

    @Autowired
    public StreamConsumer(DTOConverter dtoConverter, AttendeeRepository attendeeRepository, EventRepository eventRepository) {
        this.dtoConverter = dtoConverter;
        this.attendeeRepository = attendeeRepository;
        this.eventRepository = eventRepository;
    }

    /**
     * Consume incoming {@link AttendeeDTO} from the message broker of the
     * user-management topic to convert all users with the role 'attendee'
     * them to the internal {@link Attendee} and persist it in the database
     */
    @Bean
    public Consumer<AttendeeDTO> consumeUserRegistration() {
        return userEntry -> {
            if (userEntry.getRole().equalsIgnoreCase("Attendee")) {
                Attendee attendee = dtoConverter.convertToAttendee(userEntry);
                attendeeRepository.saveAndFlush(attendee);
            }
        };
    }


    /**
     * Consume incoming {@link EventDTO} from the message broker of the
     * event-inventory topic to convert  them to the internal {@link Event}
     * and persist it in the database
     */
    @Bean
    public Consumer<EventDTO> consumeNewEventEntry() {
        return eventEntry -> {
            Event event = dtoConverter.convertToEvent(eventEntry);
            eventRepository.saveAndFlush(event);
        };
    }

    /**
     * Consume incoming {@link EventUpdateDTO} from the message broker of the
     * to update the start / end-date of the Event
     */
    @Bean
    public Consumer<EventUpdateDTO> consumeEventUpdate() {
        return eventUpdateEntry -> {
            Optional<Event> updatedEvent = eventRepository.findById(eventUpdateEntry.getEventId());
            if (updatedEvent.isPresent()) {
                updatedEvent.get().setStartDate(eventUpdateEntry.getNewStartDate());
                updatedEvent.get().setEndDate(eventUpdateEntry.getNewEndDate());
                eventRepository.saveAndFlush(updatedEvent.get());
            }
        };
    }
}
