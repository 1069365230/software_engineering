package com.jin.service.exportservice.model.service;

import com.jin.service.exportservice.kafka.utility.RequestType;
import com.jin.service.exportservice.model.AttendingEvent;
import com.jin.service.exportservice.model.BookmarkedEvent;
import com.jin.service.exportservice.model.Event;
import com.jin.service.exportservice.repo.AttendingRepository;
import com.jin.service.exportservice.repo.BookmarkedRepository;
import com.jin.service.exportservice.repo.EventRepository;
import com.jin.service.exportservice.service.ExportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class ExportServiceTopicProcessingTest {
    @Mock
    private EventRepository eventRepositoryDummy;
    @Mock
    private AttendingRepository attendingRepositoryDummy;
    @Mock
    private BookmarkedRepository bookmarkedRepositoryDummy;
    private ExportService exportServiceDummy;
    private List<String> dummyTags;
    private Event dummyEvent;

    @BeforeEach
    void setUp() {
        exportServiceDummy = new ExportService(eventRepositoryDummy, attendingRepositoryDummy, bookmarkedRepositoryDummy);

        dummyTags = new ArrayList<>();
        dummyTags.add("EDUCATION");
        dummyTags.add("SPORT");
        dummyTags.add("FOOD");

        dummyEvent = new Event();
        dummyEvent.setName("dummyEvent");
        dummyEvent.setGlobalId(1);
        dummyEvent.setLocation("dummyLocation");
        dummyEvent.setDate("2023/9/12");
    }

    @Test
    void handlingAddAttendance() {
        //prerequisite
        String dummyData = "{\"attendeeId\":1,\"eventId\":1,\"tags\":[\"SPORT\",\"EDUCATION\"],\"action\":\"true\"}";
        doReturn(dummyEvent).when(eventRepositoryDummy).findAllByGlobalEventId(1);

        //function that is being tested
        exportServiceDummy.processIncomingAttendanceTopic(dummyData);

        //captured
        ArgumentCaptor<AttendingEvent> attendingEventArgumentCaptor = ArgumentCaptor.forClass(AttendingEvent.class);
        Mockito.verify(attendingRepositoryDummy).save(attendingEventArgumentCaptor.capture());
        AttendingEvent capturedAttendingEvent = attendingEventArgumentCaptor.getValue();

        //verifying
        int expectedAttendingEventGlobalID = 1;
        int actualAttendingEventGlobalID = capturedAttendingEvent.getGlobalEventID();
        assertEquals(expectedAttendingEventGlobalID, actualAttendingEventGlobalID);

        int expectedAttendingAttendeeGlobalID = 1;
        int actualAttendingAttendeeGlobalID = capturedAttendingEvent.getGlobalAttendeeID();
        assertEquals(expectedAttendingAttendeeGlobalID, actualAttendingAttendeeGlobalID);
    }

    @Test
    void handlingRemoveAttendance() {
        //prerequisite
        String dummyData = "{\"attendeeId\":1,\"eventId\":1,\"tags\":[\"SPORT\",\"EDUCATION\"],\"action\":\"false\"}";
        AttendingEvent dummyAttendingEvent = new AttendingEvent();
        dummyAttendingEvent.setGlobalAttendeeID(1);
        dummyAttendingEvent.setGlobalEventID(1);
        doReturn(Optional.of(dummyAttendingEvent)).when(attendingRepositoryDummy).findAllByGlobalAttendeeIdAndEventId(1,1);

        //function that is being tested
        exportServiceDummy.processIncomingAttendanceTopic(dummyData);

        //captured
        ArgumentCaptor<AttendingEvent> attendingEventArgumentCaptor = ArgumentCaptor.forClass(AttendingEvent.class);
        Mockito.verify(attendingRepositoryDummy).delete(attendingEventArgumentCaptor.capture());

        AttendingEvent capturedAttendingEvent = attendingEventArgumentCaptor.getValue();

        //verifying
        int expectedAttendingEventGlobalID = 1;
        int actualAttendingEventGlobalID = capturedAttendingEvent.getGlobalEventID();
        assertEquals(expectedAttendingEventGlobalID, actualAttendingEventGlobalID);

        int expectedAttendingAttendeeGlobalID = 1;
        int actualAttendingAttendeeGlobalID = capturedAttendingEvent.getGlobalAttendeeID();
        assertEquals(expectedAttendingAttendeeGlobalID, actualAttendingAttendeeGlobalID);
    }

    @Test
    void handlingAddBookmark() {
        //prerequisite
        String dummyData = "{\"attendeeId\":1,\"eventId\":1,\"tags\":[\"SPORT\",\"EDUCATION\"],\"action\":\"true\"}";
        doReturn(dummyEvent).when(eventRepositoryDummy).findAllByGlobalEventId(1);

        //function that is being tested
        exportServiceDummy.processIncomingBookmarkTopic(dummyData);

        //captured
        ArgumentCaptor<BookmarkedEvent> bookmarkedEventArgumentCaptor = ArgumentCaptor.forClass(BookmarkedEvent.class);
        Mockito.verify(bookmarkedRepositoryDummy).save(bookmarkedEventArgumentCaptor.capture());
        BookmarkedEvent capturedBookmarkEvent = bookmarkedEventArgumentCaptor.getValue();

        //verifying
        int expectedBookmarkEventGlobalID = 1;
        int actualBookmarkEventGlobalID = capturedBookmarkEvent.getGlobalEventID();
        assertEquals(expectedBookmarkEventGlobalID, actualBookmarkEventGlobalID);

        int expectedBookmarkAttendeeGlobalID = 1;
        int actualBookmarkAttendeeGlobalID = capturedBookmarkEvent.getGlobalAttendeeID();
        assertEquals(expectedBookmarkAttendeeGlobalID, actualBookmarkAttendeeGlobalID);
    }

    @Test
    void handlingRemoveBookmark() {
        //prerequisite
        String dummyData = "{\"attendeeId\":1,\"eventId\":1,\"tags\":[\"SPORT\",\"EDUCATION\"],\"action\":\"false\"}";
        BookmarkedEvent bookmarkedEventDummy = new BookmarkedEvent();
        bookmarkedEventDummy.setGlobalEventID(1);
        bookmarkedEventDummy.setGlobalAttendeeID(1);
        doReturn(Optional.of(bookmarkedEventDummy)).when(bookmarkedRepositoryDummy).findAllByGlobalAttendeeIdAndEventId(1,1);

        //function that is being tested
        exportServiceDummy.processIncomingBookmarkTopic(dummyData);

        //captured
        ArgumentCaptor<BookmarkedEvent> bookmarkedEventArgumentCaptor = ArgumentCaptor.forClass(BookmarkedEvent.class);
        Mockito.verify(bookmarkedRepositoryDummy).delete(bookmarkedEventArgumentCaptor.capture());
        BookmarkedEvent capturedBookmarkEvent = bookmarkedEventArgumentCaptor.getValue();

        //verifying
        int expectedBookmarkEventGlobalID = 1;
        int actualBookmarkEventGlobalID = capturedBookmarkEvent.getGlobalEventID();
        assertEquals(expectedBookmarkEventGlobalID, actualBookmarkEventGlobalID);

        int expectedBookmarkAttendeeGlobalID = 1;
        int actualBookmarkAttendeeGlobalID = capturedBookmarkEvent.getGlobalAttendeeID();
        assertEquals(expectedBookmarkAttendeeGlobalID, actualBookmarkAttendeeGlobalID);
    }

    @Test
    void handlingAddTagTopic() {
        //prerequisite
        String dummyData = "{\"attendeeId\":1,\"eventId\":1,\"tag\":\"EDUCATION\",\"action\":\"true\"}";
        String dummyData1 = "{\"attendeeId\":1,\"eventId\":1,\"tag\":\"SPORT\",\"action\":\"true\"}";
        String dummyData2 = "{\"attendeeId\":1,\"eventId\":1,\"tag\":\"FOOD\",\"action\":\"true\"}";
        doReturn(dummyEvent).when(eventRepositoryDummy).findAllByGlobalEventId(1);

        //prerequisite, mock it that attending, bookmark and event are in the dbs already
        AttendingEvent dummyAttendingEvent = new AttendingEvent();
        dummyAttendingEvent.setGlobalEventID(1);
        dummyAttendingEvent.setGlobalAttendeeID(1);
        doReturn(Optional.of(dummyAttendingEvent)).when(attendingRepositoryDummy).findAllByGlobalAttendeeIdAndEventId( 1,1);

        List<AttendingEvent> dummyAttendingEvents = new ArrayList<>();
        dummyAttendingEvents.add(dummyAttendingEvent);
        doReturn(dummyAttendingEvents).when(attendingRepositoryDummy).findAllByGlobalAttendeeId( 1);

        BookmarkedEvent dummyBookmarkedEvent = new BookmarkedEvent();
        dummyBookmarkedEvent.setGlobalEventID(1);
        dummyBookmarkedEvent.setGlobalAttendeeID(1);
        doReturn(Optional.of(dummyBookmarkedEvent)).when(bookmarkedRepositoryDummy).findAllByGlobalAttendeeIdAndEventId( 1,1);

        List<BookmarkedEvent> dummyBookmarkEvents = new ArrayList<>();
        dummyBookmarkEvents.add(dummyBookmarkedEvent);
        doReturn(dummyBookmarkEvents).when(bookmarkedRepositoryDummy).findAllByGlobalAttendeeId( 1);

        //function that is being tested, 3 incoming tags
        exportServiceDummy.processIncomingTagTopic(dummyData);
        exportServiceDummy.processIncomingTagTopic(dummyData1);
        exportServiceDummy.processIncomingTagTopic(dummyData2);

        //verifying
        List<Event> actualBookmarkedEvents = exportServiceDummy.getAllBookMarkEvents(1);
        List<String> actualTagsOfBookmark = actualBookmarkedEvents.get(0).getTags();
        List<String> expectedTagsOfBookmark = dummyTags;

        List<Event> actualAttendingEvents = exportServiceDummy.getAllAttendingEvents(1);
        List<String> actualTagsOfAttending = actualAttendingEvents.get(0).getTags();
        List<String> expectedTagsOfAttending = dummyTags;

        assertEquals(expectedTagsOfBookmark, actualTagsOfBookmark);
        assertEquals(expectedTagsOfAttending, actualTagsOfAttending);
    }

    @Test
    void handlingRemoveTagTopic() {
        //prerequisite
        String dummyData = "{\"attendeeId\":1,\"eventId\":1,\"tag\":\"FOOD\",\"action\":\"false\"}";

        //prerequisite, mock it that attending, bookmark and event are in the dbs already
        AttendingEvent dummyAttendingEvent = new AttendingEvent();
        dummyAttendingEvent.setGlobalEventID(1);
        dummyAttendingEvent.setGlobalAttendeeID(1);
        dummyAttendingEvent.setTags(dummyTags);
        doReturn(Optional.of(dummyAttendingEvent)).when(attendingRepositoryDummy).findAllByGlobalAttendeeIdAndEventId( 1,1);

        BookmarkedEvent dummyBookmarkedEvent = new BookmarkedEvent();
        dummyBookmarkedEvent.setGlobalEventID(1);
        dummyBookmarkedEvent.setGlobalAttendeeID(1);
        dummyBookmarkedEvent.setTags(dummyTags);
        doReturn(Optional.of(dummyBookmarkedEvent)).when(bookmarkedRepositoryDummy).findAllByGlobalAttendeeIdAndEventId( 1,1);

        //function that is being tested
        exportServiceDummy.processIncomingTagTopic(dummyData);

        //verifying
        ArgumentCaptor<AttendingEvent> attendingEventArgumentCaptor = ArgumentCaptor.forClass(AttendingEvent.class);
        Mockito.verify(attendingRepositoryDummy).save(attendingEventArgumentCaptor.capture());
        AttendingEvent capturedAttendingEvent = attendingEventArgumentCaptor.getValue();

        ArgumentCaptor<BookmarkedEvent> bookmarkEventArgumentCaptor = ArgumentCaptor.forClass(BookmarkedEvent.class);
        Mockito.verify(bookmarkedRepositoryDummy).save(bookmarkEventArgumentCaptor.capture());
        AttendingEvent capturedBookmarkEvent = attendingEventArgumentCaptor.getValue();

        dummyTags.remove("FOOD");
        List<String> expectedTags = dummyTags;

        List<String> actualTagsOfBookmark = capturedAttendingEvent.getTags();
        List<String> actualTagsOfAttending = capturedBookmarkEvent.getTags();

        assertEquals(expectedTags, actualTagsOfBookmark);
        assertEquals(expectedTags, actualTagsOfAttending);
    }
}
