package com.ase.recommenderservice.jpa;

import com.ase.recommenderservice.model.Attendee;
import com.ase.recommenderservice.model.Event;
import com.ase.recommenderservice.model.EventCategory;
import com.ase.recommenderservice.repository.AttendeeRepository;
import com.ase.recommenderservice.repository.EventRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@DataJpaTest
@ActiveProfiles("test")
public class EventCandidateTest {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private AttendeeRepository attendeeRepository;


    @Test
    public void eventCandidateQuery_ShouldReturnValidEvents() {
        // GIVEN
        Event validEvent = createEvent(1, 200, 2);
        Event noVacanciesEvent = createEvent(2, 0, 2);
        Event outdatedEvent = createEvent(3, 200, -5);
        Event alreadyAttending = createEvent(2, 200, 2);

        Attendee testAttendee = new Attendee(1l, "country", "city", "email");
        testAttendee.setAttendingEvents(Arrays.asList(alreadyAttending));

        eventRepository.saveAll(Arrays.asList(validEvent, noVacanciesEvent, outdatedEvent, alreadyAttending));
        attendeeRepository.saveAndFlush(testAttendee);

        // WHEN
        List<Event> eventCandidates = eventRepository.retrieveEventCandidates(1l);

        // THEN
        Assertions.assertTrue(eventCandidates.size() == 1);
        Assertions.assertTrue(eventCandidates.contains(validEvent));
    }


    private Event createEvent(long id, int vacancies, int startDateInDays) {
        return new Event(id, new EventCategory("Category"), "Country", "City",
                "EventName", vacancies,
                Instant.now().plus(Duration.ofDays(startDateInDays)), Instant.now().plus(Duration.ofDays(startDateInDays + 1)));
    }
}
