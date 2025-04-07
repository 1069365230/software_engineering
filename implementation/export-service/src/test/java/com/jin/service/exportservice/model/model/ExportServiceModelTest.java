package com.jin.service.exportservice.model.model;

import com.jin.service.exportservice.model.AttendingEvent;
import com.jin.service.exportservice.model.BookmarkedEvent;
import com.jin.service.exportservice.model.Event;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ExportServiceModelTest {
    @Test
    void eventModelTest() {
        List<String> dummyTags = new ArrayList<>();
        dummyTags.add("SPORT");

        Event event = new Event();
        event.setName("dummyEvent");
        event.setGlobalId(1);
        event.setLocation("dummyLocation");
        event.setDate("2023/3/6");
        event.setTags(dummyTags);

        int expectedGlobalID = 1;
        int actualGlobalID = event.getGlobalId();
        assertEquals(expectedGlobalID, actualGlobalID);

        String expectedLocation = "dummyLocation";
        String actualLocation = event.getLocation();
        assertEquals(expectedLocation, actualLocation);

        String expectedName = "dummyEvent";
        String actualName = event.getName();
        assertEquals(expectedName, actualName);

        String expectedDate = "2023/3/6";
        String actualDate = event.getDate();
        assertEquals(expectedDate, actualDate);

        boolean expectedTags = event.getTags().contains("SPORT");
        assertTrue(expectedTags);

    }

    @Test
    void bookmarkedEventModelTest() {
        BookmarkedEvent dummyBookmarkedEvent = new BookmarkedEvent();
        dummyBookmarkedEvent.setGlobalEventID(1);
        dummyBookmarkedEvent.setGlobalAttendeeID(1);

        int expectedEventGlobalID = 1;
        int actualEventGlobalID = dummyBookmarkedEvent.getGlobalEventID();
        assertEquals(expectedEventGlobalID, actualEventGlobalID);

        int expectedAttendeeGlobalID = 1;
        int actualAttendeeGlobalID = dummyBookmarkedEvent.getGlobalAttendeeID();
        assertEquals(expectedAttendeeGlobalID, actualAttendeeGlobalID);
    }

    @Test
    void attendingEventModelTest() {
        AttendingEvent dummyAttendingEvent = new AttendingEvent();
        dummyAttendingEvent.setGlobalEventID(1);
        dummyAttendingEvent.setGlobalAttendeeID(1);

        int expectedEventGlobalID = 1;
        int actualEventGlobalID = dummyAttendingEvent.getGlobalEventID();
        assertEquals(expectedEventGlobalID, actualEventGlobalID);

        int expectedAttendeeGlobalID = 1;
        int actualAttendeeGlobalID = dummyAttendingEvent.getGlobalAttendeeID();
        assertEquals(expectedAttendeeGlobalID, actualAttendeeGlobalID);
    }

}
