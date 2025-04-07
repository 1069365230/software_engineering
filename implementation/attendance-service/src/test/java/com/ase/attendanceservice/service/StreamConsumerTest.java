package com.ase.attendanceservice.service;

import com.ase.attendanceservice.consumer.StreamConsumer;
import com.ase.attendanceservice.model.Event;
import com.ase.attendanceservice.model.dto.incoming.AttendeeDTO;
import com.ase.attendanceservice.model.dto.incoming.EventDTO;
import com.ase.attendanceservice.model.dto.incoming.EventUpdateDTO;
import com.ase.attendanceservice.repository.AttendeeRepository;
import com.ase.attendanceservice.repository.EventRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.time.Instant;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext
public class StreamConsumerTest {
    @Autowired
    private StreamConsumer streamConsumer;

    @Autowired
    private AttendeeRepository attendeeRepository;

    @Autowired
    private EventRepository eventRepository;

    @Test
    @Transactional
    public void receiveAttendeeDTO_ShouldBePersistedCorrectly() {
        streamConsumer.consumeUserRegistration().accept(
                new AttendeeDTO(10l, "test@example.com", "firstname", "lastname", "Attendee"));

        Assertions.assertTrue(attendeeRepository.findById(10l).isPresent());
    }

    @Test
    @Transactional
    public void receiveOrganizerDTO_ShouldNotBePersisted() {
        streamConsumer.consumeUserRegistration().accept(
                new AttendeeDTO(1l, "test@example.com", "firstname", "lastname", "Organizer"));

        Assertions.assertTrue(attendeeRepository.findById(1l).isEmpty());
    }

    @Test
    @Transactional
    public void receiveEventDTO_ShouldBePersisted() {
        streamConsumer.consumeNewEventEntry().accept(
                new EventDTO(2l, 25l, "eventname",
                        100, Instant.now(), Instant.now().plus(Duration.ofDays(2))));

        Assertions.assertTrue(eventRepository.findById(2l).isPresent());
    }

    @Test
    @Transactional
    public void receiveEventUpdateDTO_ShouldUpdateEventPersisted() {
        // GIVEN
        Instant oldStartDate = Instant.now();
        Instant oldEndDate = oldStartDate.plus(Duration.ofDays(1));
        Event outdatedEvent = new Event(15l, 25l, "name", 200,
                oldStartDate, oldEndDate);
        eventRepository.saveAndFlush(outdatedEvent);

        // WHEN
        Instant newStartDate = oldStartDate.plus(Duration.ofDays(1)); // Move Event one day later
        Instant newEndDate = oldEndDate.plus(Duration.ofDays(1));
        streamConsumer.consumeEventUpdate().accept(
                new EventUpdateDTO(15l, newStartDate, newEndDate));

        // THEN
        Event updatedEvent = eventRepository.findById(15l).get();
        Assertions.assertEquals(newStartDate, updatedEvent.getStartDate());
        Assertions.assertEquals(newEndDate, updatedEvent.getEndDate());
    }
}
