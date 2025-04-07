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

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EventRepoTest {
    @Autowired
    private EventRepository dummyEventRepository;
    private Attendee attendeeDummy;
    private int internalSequenceID = 1;

    @BeforeAll
    public void setUp() {
        attendeeDummy = new Attendee();
        attendeeDummy.setGlobalId(1);
    }

    @AfterEach
    public void increaseSequenceID() {
        internalSequenceID += 1;
    }

    @Test
    public void insertEventTest() {
        //prerequisite
        Event eventDummy = new Event();
        eventDummy.setGlobalId(1);
        eventDummy.setBookmark(true);
        eventDummy.addTag("EDUCATION");
        eventDummy.setAttendee(attendeeDummy);

        //function that is being tested
        dummyEventRepository.save(eventDummy);

        //verifying
        Event expectedEvent = eventDummy;
        Event actualEvent = dummyEventRepository.findById(internalSequenceID).orElse(null);
        assertEquals(expectedEvent, actualEvent);
    }

    @Test
    public void removeEventTest() {
        //prerequisite
        Event eventDummy = new Event();
        eventDummy.setGlobalId(1);
        eventDummy.setBookmark(true);
        eventDummy.addTag("EDUCATION");
        eventDummy.setAttendee(attendeeDummy);
        dummyEventRepository.save(eventDummy);

        //function that is being tested
        dummyEventRepository.deleteById(internalSequenceID);

        //verifying
        Event actualEvent = dummyEventRepository.findById(internalSequenceID).orElse(null);
        assertNull(actualEvent);
    }

    @Test
    public void updateEventTest() {
        //prerequisite
        Event eventDummy = new Event();
        eventDummy.setGlobalId(1);
        eventDummy.setBookmark(true);
        eventDummy.addTag("EDUCATION");
        eventDummy.setAttendee(attendeeDummy);
        dummyEventRepository.save(eventDummy);

        Event updatedEvent = dummyEventRepository.findById(internalSequenceID).orElse(null);
        updatedEvent.addTag("SPORT");
        updatedEvent.setGlobalId(3);

        //function that is being tested
        dummyEventRepository.save(updatedEvent);

        //verifying
        Event actualEvent = dummyEventRepository.findById(internalSequenceID).orElse(null);
        eventDummy.addTag("SPORT");
        Event expectedEvent = eventDummy;

        assertEquals(expectedEvent, actualEvent);
    }

}