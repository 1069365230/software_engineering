package com.jin.service.marktagservice.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MarktagServiceModelTest {

    private Event dummyEvent1 = new Event();
    private Event dummyEvent2 = new Event();
    private Event dummyEvent3 = new Event();

    private List<Event> dummyEvents = new ArrayList<>();
    private List<Tag> dummyTags =  new ArrayList<>();

    @BeforeAll
    void setUp() {
        //Event dummyEvent1 = new Event();
        dummyEvent1.setId(1);
        dummyEvent1.setGlobalId(1);

        //Event dummyEvent2 = new Event();
        dummyEvent2.setId(2);
        dummyEvent2.setGlobalId(2);

        //Event dummyEvent3 = new Event();
        dummyEvent3.setId(3);
        dummyEvent3.setGlobalId(3);

        dummyEvents.add(dummyEvent1);
        dummyEvents.add(dummyEvent2);

        dummyTags.add(Tag.EDUCATION);
        dummyTags.add(Tag.SPORT);
    }


    @Test
    void attendeeModelTest() {
        Attendee attendee = new Attendee();
        attendee.setGlobalId(1);
        attendee.setId(1);
        attendee.setEvents(dummyEvents);

        int expectedGlobalID = 1;
        int actualGlobalID = attendee.getGlobalId();
        assertEquals(expectedGlobalID, actualGlobalID);

        int expectedID = 1;
        int actualID = attendee.getId();
        assertEquals(expectedID, actualID);

        List<Event> expectedEvents = dummyEvents;
        List<Event> actualEvents = attendee.getEvents();
        assertEquals(expectedEvents, actualEvents);
    }

    @Test
    void attendeeLogicTest() {
        Attendee attendee = new Attendee();
        attendee.setGlobalId(1);
        attendee.setId(1);
        attendee.setEvents(dummyEvents);

        Event expectedEvent = dummyEvent1;
        Event actualEvent = attendee.findEventByGlobalId(1);
        assertEquals(expectedEvent, actualEvent);

        attendee.addEvent(dummyEvent3);
        dummyEvents.add(dummyEvent3);
        List<Event> expectedExpandedEvents = dummyEvents;
        List<Event> actualExpandedEvents = attendee.getEvents();
        assertEquals(expectedExpandedEvents, actualExpandedEvents);

        attendee.removeEvent(dummyEvent2);
        dummyEvents.remove(dummyEvent2);
        List<Event> expectedShrunkEvents = dummyEvents;
        List<Event> actualShrunkEvents = attendee.getEvents();
        assertEquals(expectedShrunkEvents, actualShrunkEvents);
    }

    @Test
    void eventModelTest() {
        Event event = new Event();
        event.setGlobalId(1);
        event.setId(1);
        event.setBookmark(true);
        event.setTags(dummyTags);

        int expectedID = 1;
        int actualID = event.getId();
        assertEquals(expectedID, actualID);

        int expectedGlobalID = 1;
        int actualGlobalID = event.getGlobalId();
        assertEquals(expectedGlobalID, actualGlobalID);

        boolean expectedBookmark = true;
        boolean actualBookmark = event.getBookmark();
        assertEquals(expectedBookmark, actualBookmark);

        List<Tag> expectedTags = dummyTags;
        List<Tag> actualTags = event.getTags();
        assertEquals(expectedTags, actualTags);
    }

    @Test
    void eventLogicTest() {
        Event event = new Event();
        event.setGlobalId(1);
        event.setId(1);
        event.setBookmark(true);
        event.setTags(dummyTags);

        event.addTag("FOOD");
        dummyTags.add(Tag.FOOD);

        List<Tag> expectedExpandedTags = dummyTags;
        List<Tag> actualExpandedTags = event.getTags();
        assertEquals(expectedExpandedTags, actualExpandedTags);

        event.removeTag("SPORT");
        dummyTags.remove(Tag.SPORT);
        List<Tag> expectedShrunkTags = dummyTags;
        List<Tag> actualShrunkTags = event.getTags();
        assertEquals(expectedShrunkTags, actualShrunkTags);
    }

}
