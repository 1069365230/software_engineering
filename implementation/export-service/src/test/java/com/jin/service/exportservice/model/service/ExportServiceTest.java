package com.jin.service.exportservice.model.service;

import com.jin.service.exportservice.factory.ExportFactory;
import com.jin.service.exportservice.kafka.utility.RequestType;
import com.jin.service.exportservice.model.AttendingEvent;
import com.jin.service.exportservice.model.BookmarkedEvent;
import com.jin.service.exportservice.model.Event;
import com.jin.service.exportservice.model.FileType;
import com.jin.service.exportservice.repo.AttendingRepository;
import com.jin.service.exportservice.repo.BookmarkedRepository;
import com.jin.service.exportservice.repo.EventRepository;
import com.jin.service.exportservice.service.ExportService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class ExportServiceTest {
    @Mock
    private EventRepository eventRepositoryDummy;
    @Mock
    private AttendingRepository attendingRepositoryDummy;
    @Mock
    private BookmarkedRepository bookmarkedRepositoryDummy;

    private ExportService exportServiceDummy;

    private static List<String> dummyTags = new ArrayList<>();

    @BeforeAll
    public static void setUpDummyTags() {
        dummyTags.add("EDUCATION");
        dummyTags.add("SPORT");
        dummyTags.add("FOOD");
    }

    @BeforeEach
    void setUp() {
        exportServiceDummy = new ExportService(eventRepositoryDummy, attendingRepositoryDummy, bookmarkedRepositoryDummy);
    }

    @Test
    void addEventTest() {
        //prerequisite
        Event dummyEvent = new Event();
        dummyEvent.setName("dummyEvent");
        dummyEvent.setGlobalId(1);
        dummyEvent.setLocation("dummyLocation");
        dummyEvent.setDate("2023/3/6");
        dummyEvent.setTags(dummyTags);

        //function that is being tested
        exportServiceDummy.addEvent(dummyEvent);

        //captured
        ArgumentCaptor<Event> eventArgumentCaptor = ArgumentCaptor.forClass(Event.class);
        Mockito.verify(eventRepositoryDummy).save(eventArgumentCaptor.capture());
        Event capturedEvent = eventArgumentCaptor.getValue();

        //verifying
        int expectedGlobalID = 1;
        int actualGlobalID = capturedEvent.getGlobalId();
        assertEquals(expectedGlobalID, actualGlobalID);

        String expectedLocation = "dummyLocation";
        String actualLocation = capturedEvent.getLocation();
        assertEquals(expectedLocation, actualLocation);

        String expectedName = "dummyEvent";
        String actualName = capturedEvent.getName();
        assertEquals(expectedName, actualName);

        String expectedDate = "2023/3/6";
        String actualDate = capturedEvent.getDate();
        assertEquals(expectedDate, actualDate);

        boolean expectedTags = capturedEvent.getTags().contains("SPORT");
        assertTrue(expectedTags);
    }

    @Test
    void removeEventTest() {
        //prerequisite
        Event dummyEvent = new Event();
        dummyEvent.setName("dummyEvent");
        dummyEvent.setGlobalId(1);
        dummyEvent.setLocation("dummyLocation");
        dummyEvent.setDate("2023/9/12");
        dummyEvent.setTags(dummyTags);
        doReturn(dummyEvent).when(eventRepositoryDummy).findAllByGlobalEventId(1);

        //function that is being tested
        exportServiceDummy.removeEvent(1);

        //captured
        ArgumentCaptor<Event> eventArgumentCaptor = ArgumentCaptor.forClass(Event.class);
        Mockito.verify(eventRepositoryDummy).delete(eventArgumentCaptor.capture());
        Event capturedEvent = eventArgumentCaptor.getValue();

        //verifying
        int expectedGlobalID = 1;
        int actualGlobalID = capturedEvent.getGlobalId();
        assertEquals(expectedGlobalID, actualGlobalID);

        String expectedLocation = "dummyLocation";
        String actualLocation = capturedEvent.getLocation();
        assertEquals(expectedLocation, actualLocation);

        String expectedName = "dummyEvent";
        String actualName = capturedEvent.getName();
        assertEquals(expectedName, actualName);

        String expectedDate = "2023/9/12";
        String actualDate = capturedEvent.getDate();
        assertEquals(expectedDate, actualDate);

        boolean expectedTags = capturedEvent.getTags().contains("EDUCATION");
        assertTrue(expectedTags);
    }

    @Test
    void addAttendingEventTest() {
        //prerequisite
        Event dummyEvent = new Event();
        doReturn(dummyEvent).when(eventRepositoryDummy).findAllByGlobalEventId(1);
        List<String> tags = new ArrayList<>();
        tags.add("FOOD");

        //function that is being tested
        exportServiceDummy.addAttendingEvent(1,1, tags);

        //captured
        ArgumentCaptor<AttendingEvent> attendingEventArgumentCaptor = ArgumentCaptor.forClass(AttendingEvent.class);
        Mockito.verify(attendingRepositoryDummy).save(attendingEventArgumentCaptor.capture());
        AttendingEvent capturedAttendingEvent = attendingEventArgumentCaptor.getValue();

        //verifying
        int expectedEventGlobalID = 1;
        int actualEventGlobalID = capturedAttendingEvent.getGlobalEventID();
        assertEquals(expectedEventGlobalID, actualEventGlobalID);

        int expectedAttendeeGlobalID = 1;
        int actualAttendeeGlobalID = capturedAttendingEvent.getGlobalAttendeeID();
        assertEquals(expectedAttendeeGlobalID, actualAttendeeGlobalID);

        List<String> expectedTags = tags;
        List<String> actualTags = capturedAttendingEvent.getTags();
        assertEquals(expectedTags, actualTags);
    }

    @Test
    void removeAttendingEventTest() {
        //prerequisite
        AttendingEvent dummyAttendingEvent = new AttendingEvent();
        dummyAttendingEvent.setGlobalEventID(2);
        dummyAttendingEvent.setGlobalAttendeeID(2);
        doReturn(Optional.of(dummyAttendingEvent)).when(attendingRepositoryDummy).findAllByGlobalAttendeeIdAndEventId(2,2);

        //function that is being tested
        exportServiceDummy.removeAttendingEvent(2,2);

        //captured
        ArgumentCaptor<AttendingEvent> attendingEventArgumentCaptor = ArgumentCaptor.forClass(AttendingEvent.class);
        Mockito.verify(attendingRepositoryDummy).delete(attendingEventArgumentCaptor.capture());
        AttendingEvent capturedAttendingEvent = attendingEventArgumentCaptor.getValue();

        //verifying
        int expectedEventGlobalID = 2;
        int actualEventGlobalID = capturedAttendingEvent.getGlobalEventID();
        assertEquals(expectedEventGlobalID, actualEventGlobalID);

        int expectedAttendeeGlobalID = 2;
        int actualAttendeeGlobalID = capturedAttendingEvent.getGlobalAttendeeID();
        assertEquals(expectedAttendeeGlobalID, actualAttendeeGlobalID);
    }

    @Test
    void addBookmarkedEventTest() {
        //prerequisite
        Event dummyEvent = new Event();
        doReturn(dummyEvent).when(eventRepositoryDummy).findAllByGlobalEventId(3);
        List<String> tags = new ArrayList<>();
        tags.add("EDUCATION");

        //function that is being tested
        exportServiceDummy.addBookmarkedEvent(3,3, tags);

        //captured
        ArgumentCaptor<BookmarkedEvent> bookmarkedEventArgumentCaptor = ArgumentCaptor.forClass(BookmarkedEvent.class);
        Mockito.verify(bookmarkedRepositoryDummy).save(bookmarkedEventArgumentCaptor.capture());
        BookmarkedEvent capturedBookmarkedEvent = bookmarkedEventArgumentCaptor.getValue();

        //verifying
        int expectedEventGlobalID = 3;
        int actualEventGlobalID = capturedBookmarkedEvent.getGlobalEventID();
        assertEquals(expectedEventGlobalID, actualEventGlobalID);

        int expectedAttendeeGlobalID = 3;
        int actualAttendeeGlobalID = capturedBookmarkedEvent.getGlobalAttendeeID();
        assertEquals(expectedAttendeeGlobalID, actualAttendeeGlobalID);

        List<String> expectedTags = tags;
        List<String> actualTags = capturedBookmarkedEvent.getTags();
        assertEquals(expectedTags, actualTags);
    }

    @Test
    void removeBookmarkedEventTest() {
        //prerequisite
        BookmarkedEvent dummyBookmarkedEvent = new BookmarkedEvent();
        dummyBookmarkedEvent.setGlobalAttendeeID(4);
        dummyBookmarkedEvent.setGlobalEventID(4);
        doReturn(Optional.of(dummyBookmarkedEvent)).when(bookmarkedRepositoryDummy).findAllByGlobalAttendeeIdAndEventId(4,4);

        //function that is being tested
        exportServiceDummy.removeBookmarkedEvent(4,4);

        //captured
        ArgumentCaptor<BookmarkedEvent> bookmarkedEventArgumentCaptor = ArgumentCaptor.forClass(BookmarkedEvent.class);
        Mockito.verify(bookmarkedRepositoryDummy).delete(bookmarkedEventArgumentCaptor.capture());
        BookmarkedEvent capturedBookmarkedEvent = bookmarkedEventArgumentCaptor.getValue();

        //verifying
        int expectedEventGlobalID = 4;
        int actualEventGlobalID = capturedBookmarkedEvent.getGlobalEventID();
        assertEquals(expectedEventGlobalID, actualEventGlobalID);

        int expectedAttendeeGlobalID = 4;
        int actualAttendeeGlobalID = capturedBookmarkedEvent.getGlobalAttendeeID();
        assertEquals(expectedAttendeeGlobalID, actualAttendeeGlobalID);
    }


    @Test
    void getAllAttendingEventsTest() {
        //prerequisite
        List<String> dummyTags = new ArrayList<>();
        dummyTags.add("EDUCATION");

        Event dummyEvent = new Event();
        dummyEvent.setName("dummyEvent");
        dummyEvent.setGlobalId(3);
        dummyEvent.setLocation("dummyLocation");
        dummyEvent.setDate("2023/9/12");
        dummyEvent.setTags(dummyTags);

        List<Event> dummyEvents = new ArrayList<>();
        dummyEvents.add(dummyEvent);

        AttendingEvent dummyAttendingEvent = new AttendingEvent();
        dummyAttendingEvent.setGlobalEventID(1);
        dummyAttendingEvent.setGlobalAttendeeID(1);

        List<AttendingEvent> dummyAttendingEvents = new ArrayList<>();
        dummyAttendingEvents.add(dummyAttendingEvent);

        doReturn(dummyAttendingEvents).when(attendingRepositoryDummy).findAllByGlobalAttendeeId(1);
        doReturn(dummyEvent).when(eventRepositoryDummy).findAllByGlobalEventId(1);

        //function that is being tested
        List<Event> actualAttendingEvents = exportServiceDummy.getAllAttendingEvents(1);

        //verifying
        List<Event> expectedAttendingEvents = dummyEvents;
        assertEquals(expectedAttendingEvents, actualAttendingEvents);
    }

    @Test
    void getAllBookMarkEventsTest(){
        //prerequisite
        List<String> dummyTags = new ArrayList<>();
        dummyTags.add("EDUCATION");
        Event dummyEvent = new Event();
        dummyEvent.setName("dummyEvent");
        dummyEvent.setGlobalId(3);
        dummyEvent.setLocation("dummyLocation");
        dummyEvent.setDate("2023/9/12");
        dummyEvent.setTags(dummyTags);

        List<Event> dummyEvents = new ArrayList<>();
        dummyEvents.add(dummyEvent);

        BookmarkedEvent dummyBookmarkedEvent = new BookmarkedEvent();
        dummyBookmarkedEvent.setGlobalEventID(3);
        dummyBookmarkedEvent.setGlobalAttendeeID(1);
        List<BookmarkedEvent> dummyBookmarkedEvents = new ArrayList<>();
        dummyBookmarkedEvents.add(dummyBookmarkedEvent);

        doReturn(dummyBookmarkedEvents).when(bookmarkedRepositoryDummy).findAllByGlobalAttendeeId(1);
        doReturn(dummyEvent).when(eventRepositoryDummy).findAllByGlobalEventId(3);

        //function that is being tested
        List<Event> actualBookmarkedEvents = exportServiceDummy.getAllBookMarkEvents(1);

        //verifying
        List<Event> expectedBookmarkedEvents = dummyEvents;
        assertEquals(expectedBookmarkedEvents, actualBookmarkedEvents);
    }

    @Test
    void eventExistTest() {
        //prerequisite
        Event dummyEvent = new Event();
        dummyEvent.setName("dummyEvent");
        dummyEvent.setGlobalId(1);
        dummyEvent.setLocation("dummyLocation");
        dummyEvent.setDate("2023/9/12");
        doReturn(dummyEvent).when(eventRepositoryDummy).findAllByGlobalEventId(1);

        //function that is being tested
        boolean eventShouldExist = exportServiceDummy.eventExist(1);

        //verifying
        assertTrue(eventShouldExist);
    }

    @Test
    void exportFileTest() {
        //prerequisite
        List<String> dummyTags = new ArrayList<>();
        dummyTags.add("EDUCATION");
        dummyTags.add("SPORT");
        Event dummyEvent = new Event();
        dummyEvent.setName("dummyEvent");
        dummyEvent.setGlobalId(3);
        dummyEvent.setLocation("dummyLocation");
        dummyEvent.setDate("2023/9/12");
        dummyEvent.setTags(dummyTags);
        List<Event> dummyEvents = new ArrayList<>();
        dummyEvents.add(dummyEvent);
        ExportFactory factory = new ExportFactory(FileType.JSON);

        //function that is being tested
        ResponseEntity<byte[]> expectedResponseEntity = factory.produceFile(dummyEvents);

        //verifying
        ResponseEntity<byte[]> actualResponseEntity = exportServiceDummy.exportFile(FileType.JSON, dummyEvents);
        assertEquals(expectedResponseEntity, actualResponseEntity);
    }


}
