package com.jin.service.marktagservice.service;

import com.jin.service.marktagservice.model.Attendee;
import com.jin.service.marktagservice.model.Event;
import com.jin.service.marktagservice.model.Tag;
import com.jin.service.marktagservice.repo.AttendeeRepository;
import com.jin.service.marktagservice.repo.EventRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class MarkTagServiceTest {
    @Mock
    private AttendeeRepository attendeeRepositoryDummy;
    @Mock
    private EventRepository eventRepositoryDummy;
    private MarkTagService markTagServiceDummy;

    @BeforeEach
    void setUp() {
        markTagServiceDummy = new MarkTagService(eventRepositoryDummy, attendeeRepositoryDummy);
    }

    @Test
    void addAttendee() {
        //function that is being tested
        markTagServiceDummy.addAttendee(1);

        //captured
        ArgumentCaptor<Attendee> attendeeArgumentCaptor = ArgumentCaptor.forClass(Attendee.class);
        Mockito.verify(attendeeRepositoryDummy).save(attendeeArgumentCaptor.capture());
        Attendee capturedAttendee = attendeeArgumentCaptor.getValue();

        //verifying
        int expectedGlobalID = 1;
        int actualGlobalID = capturedAttendee.getGlobalId();
        assertEquals(expectedGlobalID, actualGlobalID);


    }

    @Test
    void removeAttendee() {
        //prerequisite
        Attendee attendee = new Attendee();
        attendee.setGlobalId(1);
        attendee.setId(1);
        doReturn(Optional.of(attendee)).when(attendeeRepositoryDummy).findByGlobalId(1);

        //function that is being tested
        markTagServiceDummy.removeAttendee(1);

        //captured
        ArgumentCaptor<Attendee> attendeeArgumentCaptor = ArgumentCaptor.forClass(Attendee.class);
        Mockito.verify(attendeeRepositoryDummy).delete(attendeeArgumentCaptor.capture());
        Attendee capturedAttendee = attendeeArgumentCaptor.getValue();

        int expectedGlobalID = 1;
        int actualGlobalID = capturedAttendee.getGlobalId();
        assertEquals(expectedGlobalID, actualGlobalID);
    }

    @Test
    void addEvent() {
        //prerequisite
        Attendee attendee = new Attendee();
        attendee.setGlobalId(1);
        attendee.setId(1);
        List<Attendee> allAttendees = new ArrayList<>();
        allAttendees.add(attendee);

        doReturn(allAttendees).when(attendeeRepositoryDummy).findAll();

        //function that is being tested
        markTagServiceDummy.addEvent(1, "dummy");

        //captured
        ArgumentCaptor<Attendee> attendeeArgumentCaptor = ArgumentCaptor.forClass(Attendee.class);
        Mockito.verify(attendeeRepositoryDummy).save(attendeeArgumentCaptor.capture());
        Attendee capturedAttendee = attendeeArgumentCaptor.getValue();

        Event expectedAddedEvent = capturedAttendee.findEventByGlobalId(1);
        assertNotNull(expectedAddedEvent);
    }

    @Test
    void removeEvent() {
        //prerequisite
        Event event = new Event();
        event.setGlobalId(1);
        event.setId(1);
        List<Event> events = new ArrayList<>();
        events.add(event);

        Attendee attendee = new Attendee();
        attendee.setGlobalId(1);
        attendee.setId(1);
        attendee.setEvents(events);

        List<Attendee> allAttendees = new ArrayList<>();
        allAttendees.add(attendee);

        doReturn(allAttendees).when(attendeeRepositoryDummy).findAll();

        //function that is being tested
        markTagServiceDummy.removeEvent(1);

        //captured
        ArgumentCaptor<Attendee> attendeeArgumentCaptor = ArgumentCaptor.forClass(Attendee.class);
        ArgumentCaptor<Event> eventArgumentCaptor = ArgumentCaptor.forClass(Event.class);

        Mockito.verify(eventRepositoryDummy, Mockito.times(2)).delete(eventArgumentCaptor.capture());
        Mockito.verify(attendeeRepositoryDummy).save(attendeeArgumentCaptor.capture());

        Attendee capturedAttendee = attendeeArgumentCaptor.getValue();
        List<Event> capturedEvents = eventArgumentCaptor.getAllValues();
        Event firstEvent = capturedEvents.get(0);

        //verifying
        assertThrows(NullPointerException.class, () -> {
            Event expectedRemovedEvent = capturedAttendee.findEventByGlobalId(1);
        });

        int expectedEventGID = 1;
        int actualEvent1GID = firstEvent.getGlobalId();
        assertEquals(expectedEventGID, actualEvent1GID);

        Event expectedDeletedEventFirst = firstEvent;
        assertNotNull(expectedDeletedEventFirst);
    }


    @Test
    void bookMarkEvent() {
        //prerequisite
        Event event = new Event();
        event.setGlobalId(1);
        event.setId(1);
        List<Event> events = new ArrayList<>();
        events.add(event);

        Attendee attendee = new Attendee();
        attendee.setGlobalId(1);
        attendee.setId(1);
        attendee.setEvents(events);

        doReturn(Optional.of(attendee)).when(attendeeRepositoryDummy).findByGlobalId(1);

        //function that is being tested
        markTagServiceDummy.bookMarkEvent(1, 1);

        //captured
        ArgumentCaptor<Attendee> attendeeArgumentCaptor = ArgumentCaptor.forClass(Attendee.class);
        Mockito.verify(attendeeRepositoryDummy).save(attendeeArgumentCaptor.capture());
        Attendee capturedAttendee = attendeeArgumentCaptor.getValue();

        //verifying
        Event expectedEvent = capturedAttendee.findEventByGlobalId(1);
        boolean expectedBookmark = true;
        boolean actualBookmark = expectedEvent.getBookmark();
        assertEquals(expectedBookmark, actualBookmark);
    }

    @Test
    void unbookMarkEvent() {
        //prerequisite
        Event event = new Event();
        event.setGlobalId(1);
        event.setId(1);
        event.setBookmark(true);
        List<Event> events = new ArrayList<>();
        events.add(event);

        Attendee attendee = new Attendee();
        attendee.setGlobalId(1);
        attendee.setId(1);
        attendee.setEvents(events);

        doReturn(Optional.of(attendee)).when(attendeeRepositoryDummy).findByGlobalId(1);

        //function that is being tested
        markTagServiceDummy.unbookMarkEvent(1, 1);

        //captured
        ArgumentCaptor<Attendee> attendeeArgumentCaptor = ArgumentCaptor.forClass(Attendee.class);
        Mockito.verify(attendeeRepositoryDummy).save(attendeeArgumentCaptor.capture());
        Attendee capturedAttendee = attendeeArgumentCaptor.getValue();

        //verifying
        Event expectedEvent = capturedAttendee.findEventByGlobalId(1);
        boolean expectedBookmark = false;
        boolean actualBookmark = expectedEvent.getBookmark();
        assertEquals(expectedBookmark, actualBookmark);
    }

    @Test
    void addTagOnEventByAttendee() {
        //prerequisite
        Event event = new Event();
        event.setGlobalId(1);
        event.setId(1);
        List<Event> events = new ArrayList<>();
        events.add(event);

        Attendee attendee = new Attendee();
        attendee.setGlobalId(1);
        attendee.setId(1);
        attendee.setEvents(events);

        doReturn(Optional.of(attendee)).when(attendeeRepositoryDummy).findByGlobalId(1);

        //function that is being tested
        markTagServiceDummy.addTagOnEventByAttendee(1, 1, "EDUCATION");

        //captured
        ArgumentCaptor<Attendee> attendeeArgumentCaptor = ArgumentCaptor.forClass(Attendee.class);
        Mockito.verify(attendeeRepositoryDummy).save(attendeeArgumentCaptor.capture());
        Attendee capturedAttendee = attendeeArgumentCaptor.getValue();

        //verifying
        Event expectedEvent = capturedAttendee.findEventByGlobalId(1);
        boolean tagExistsInEvent = expectedEvent.getTags().contains(Tag.EDUCATION);
        assertTrue(tagExistsInEvent);
    }

    @Test
    void removeTagOnEventByAttendee() {
        //prerequisite
        Event event = new Event();
        event.setGlobalId(1);
        event.setId(1);
        List<Event> events = new ArrayList<>();
        events.add(event);
        event.addTag("SPORT");

        Attendee attendee = new Attendee();
        attendee.setGlobalId(1);
        attendee.setId(1);
        attendee.setEvents(events);

        doReturn(Optional.of(attendee)).when(attendeeRepositoryDummy).findByGlobalId(1);

        //sane check before removing the tag
        assertTrue(attendee.findEventByGlobalId(1).getTags().contains(Tag.SPORT));

        //function that is being tested
        markTagServiceDummy.removeTagOnEventByAttendee(1, 1, "SPORT");

        //captured
        ArgumentCaptor<Attendee> attendeeArgumentCaptor = ArgumentCaptor.forClass(Attendee.class);
        Mockito.verify(attendeeRepositoryDummy).save(attendeeArgumentCaptor.capture());
        Attendee capturedAttendee = attendeeArgumentCaptor.getValue();

        //verifying
        Event expectedEvent = capturedAttendee.findEventByGlobalId(1);
        boolean tagExistsInEvent = expectedEvent.getTags().contains(Tag.SPORT);
        assertFalse(tagExistsInEvent);

    }

}