package com.jin.service.exportservice.model.repo;

import com.jin.service.exportservice.model.AttendingEvent;
import com.jin.service.exportservice.model.Event;
import com.jin.service.exportservice.repo.EventRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EventRepoTest {

    @Autowired
    private EventRepository eventRepositoryDummy;

    @AfterEach
    public void cleanUP() {
        eventRepositoryDummy.deleteAll();
    }

    @Test
    void findAllByGlobalEventIdTest() {
        //prerequisite
        Event eventDummy1 = new Event();
        eventDummy1.setName("dummy");
        eventDummy1.setGlobalId(1);
        eventDummy1.setDate("2023/5/17");
        eventDummy1.setLocation("location-dummy");

        //function that is being tested
        eventRepositoryDummy.save(eventDummy1);

        //verifying
        Event actualEvent = eventRepositoryDummy.findAllByGlobalEventId(1);
        Event expectedEvent = eventDummy1;
        assertEquals(expectedEvent, actualEvent);
    }

    @Test
    void insertEventTest() {
        //prerequisite
        Event eventDummy1 = new Event();
        eventDummy1.setName("dummy");
        eventDummy1.setGlobalId(1);
        eventDummy1.setDate("2023/5/17");
        eventDummy1.setLocation("location-dummy");

        //function that is being tested
        eventRepositoryDummy.save(eventDummy1);

        //verifying
        Event expectedEvent = eventDummy1;
        Event actualEvent = eventRepositoryDummy.findAllByGlobalEventId(1);
        assertEquals(expectedEvent, actualEvent);
    }

    @Test
    void deleteEventTest() {
        //prerequisite
        Event eventDummy1 = new Event();
        eventDummy1.setName("dummy");
        eventDummy1.setGlobalId(1);
        eventDummy1.setDate("2023/5/17");
        eventDummy1.setLocation("location-dummy");
        eventRepositoryDummy.save(eventDummy1);

        //function that is being tested
        int id = eventRepositoryDummy.findAllByGlobalEventId(1).getId();
        eventRepositoryDummy.deleteById(id);

        //verifying
        Event actualEvent = eventRepositoryDummy.findAllByGlobalEventId(1);
        assertNull(actualEvent);
    }

    @Test
    void updateEventTest() {
        //prerequisite
        Event eventDummy1 = new Event();
        eventDummy1.setName("dummy");
        eventDummy1.setGlobalId(1);
        eventDummy1.setDate("2023/5/17");
        eventDummy1.setLocation("location-dummy");
        eventRepositoryDummy.save(eventDummy1);

        //function that is being tested
        Event updateEvent = eventRepositoryDummy.findAllByGlobalEventId(1);
        updateEvent.setLocation("updated_location");
        updateEvent.setGlobalId(2);
        updateEvent.setDate("2023/6/30");
        eventRepositoryDummy.save(updateEvent);

        //verifying
        Event actualEvent = eventRepositoryDummy.findById(updateEvent.getId()).orElse(null);
        Event expectedEvent = eventDummy1;
        assertEquals(expectedEvent, actualEvent);
    }

}
