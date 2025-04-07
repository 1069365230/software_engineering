package com.jin.service.marktagservice.repo;

import com.jin.service.marktagservice.model.Attendee;
import com.jin.service.marktagservice.model.Event;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ParentChildrenRelationshipTest {
    @Autowired
    private AttendeeRepository dummyAttendeeRepository;
    @Autowired
    private EventRepository dummyEventRepository;

    @AfterEach
    public void cleanUpEvents() {
        dummyEventRepository.deleteAll();
    }

    @Test
    public void attendeeAddEventTest() {
        //prerequisite
        Attendee attendeeDummy = new Attendee();
        attendeeDummy.setGlobalId(1);
        dummyAttendeeRepository.save(attendeeDummy);
        Attendee attendee = dummyAttendeeRepository.findByGlobalId(1).orElse(null);

        Event eventDummy = new Event();
        eventDummy.setGlobalId(1);
        eventDummy.setBookmark(true);
        eventDummy.addTag("EDUCATION");
        attendee.addEvent(eventDummy);

        //function that is being tested
        dummyAttendeeRepository.save(attendee);

        //verifying (note: attendee contains an event, event repo should contain the "child" now)
        List<Event> eventsFromDummy = dummyAttendeeRepository.findByGlobalId(1).orElse(null).getEvents();
        int expectedEventsSize = 1;
        int actualEventsSize = eventsFromDummy.size();
        assertEquals(expectedEventsSize, actualEventsSize);

        //get the first event
        assertNotNull(eventsFromDummy.get(0));

        int expectedEventGlobalID = 1;
        int actualEventGlobalID = eventsFromDummy.get(0).getGlobalId();
        assertEquals(expectedEventGlobalID, actualEventGlobalID);
    }

    @Test
    public void attendeeRemoveEventTest() {
        //prerequisite
        Attendee attendeeDummy = new Attendee();
        attendeeDummy.setGlobalId(1);
        dummyAttendeeRepository.save(attendeeDummy);

        Attendee attendee = dummyAttendeeRepository.findByGlobalId(1).orElse(null);
        Event eventDummy = new Event();
        eventDummy.setGlobalId(1);
        eventDummy.setBookmark(true);
        eventDummy.addTag("EDUCATION");
        attendee.addEvent(eventDummy);
        dummyAttendeeRepository.save(attendee);

        //function that is being tested (updating the deleted event)
        Attendee updatedAttendee = dummyAttendeeRepository.findByGlobalId(1).orElse(null);
        Event event = updatedAttendee.findEventByGlobalId(1);
        updatedAttendee.getEvents().remove(event);
        dummyAttendeeRepository.save(updatedAttendee);

        //verifying
        Attendee actualAttendee = dummyAttendeeRepository.findByGlobalId(1).orElse(null);
        List<Event> eventsFromDummy = actualAttendee.getEvents();
        int expectedEventsSize = 0;
        int actualEventsSize = eventsFromDummy.size();
        assertEquals(expectedEventsSize, actualEventsSize);


        assertThrows(NullPointerException.class, () -> {
            Event removedEvent = updatedAttendee.findEventByGlobalId(1);
        });
    }

    @Test
    public void orphanRemovalTest() {
        //prerequisite
        Attendee attendeeDummy = new Attendee();
        attendeeDummy.setGlobalId(1);
        dummyAttendeeRepository.save(attendeeDummy);

        Attendee updatingAttendee = dummyAttendeeRepository.findByGlobalId(1).orElse(null);
        Event eventDummy = new Event();
        eventDummy.setGlobalId(2);
        eventDummy.addTag("FOOD");
        updatingAttendee.addEvent(eventDummy);
        dummyAttendeeRepository.save(updatingAttendee);

        //sane check before deleting parent
        long expectedEventsBeforeDel = 1;
        long actualEventsBeforeDel = dummyEventRepository.count();
        assertEquals(expectedEventsBeforeDel, actualEventsBeforeDel);

        //function that is being tested (updating the deleted event)
        //deleting parent
        dummyAttendeeRepository.deleteAll();

        //verifying (event repos should not contain anything, no orphans should exist)
        long expectedEventsAfterDel = 0;
        long actualEventsAfterDel = dummyEventRepository.count();
        assertEquals(expectedEventsAfterDel, actualEventsAfterDel);
    }
}
