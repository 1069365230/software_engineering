package com.ase.attendanceservice.jpa;


import com.ase.attendanceservice.model.Event;
import com.ase.attendanceservice.repository.EventRepository;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.time.Instant;

@DataJpaTest
@ActiveProfiles("test")
public class EventRepositoryTest {

    @Autowired
    private EventRepository eventRepository;

    @Test
    public void saveAndRetrieveEventTest() {
        // GIVEN
        Event testEvent = new Event(1l, 15l, "name", 200, Instant.now(), Instant.now().plus(Duration.ofDays(1)));

        // WHEN
        eventRepository.saveAndFlush(testEvent);

        // THEN
        Assertions.assertNotNull(eventRepository.findById(1l));
        Assertions.assertEquals(testEvent, eventRepository.findById(1l).get());
    }

    @Test
    public void eventDeletionTest() {
        // GIVEN
        Event testEvent = new Event(1l, 15l, "name", 200, Instant.now(), Instant.now().plus(Duration.ofDays(1)));

        // WHEN
        eventRepository.saveAndFlush(testEvent);
        eventRepository.delete(testEvent);

        // THEN
        Assertions.assertTrue(eventRepository.findById(1l).isEmpty());
    }

    @Test
    public void eventNullValues_ShouldThrowException() {
        // GIVEN
        Event testEvent = new Event(1l, null, "name", 200, Instant.now(), Instant.now().plus(Duration.ofDays(1)));

        // WHEN | THEN
        Assertions.assertThrows(ConstraintViolationException.class, () -> eventRepository.saveAndFlush(testEvent));
    }

    @Test
    public void decrementEventVacancy_ShouldBeUpdated() {
        // GIVEN
        Event testEvent = new Event(1l, 15l, "name", 200, Instant.now(), Instant.now().plus(Duration.ofDays(1)));
        eventRepository.saveAndFlush(testEvent);

        // WHEN
        eventRepository.decrementEventVacancy(1l);

        // THEN
        Assertions.assertEquals(199, eventRepository.findById(1l).get().getVacancies());
    }

    @Test
    public void incrementEventVacancy_ShouldBeUpdated() {
        // GIVEN
        Event testEvent = new Event(1l, 15l, "name", 200, Instant.now(), Instant.now().plus(Duration.ofDays(1)));
        testEvent.setVacancies(199);
        eventRepository.saveAndFlush(testEvent);

        // WHEN
        eventRepository.incrementEventVacancy(1l);

        // THEN
        Assertions.assertEquals(200, eventRepository.findById(1l).get().getVacancies());
    }

}
