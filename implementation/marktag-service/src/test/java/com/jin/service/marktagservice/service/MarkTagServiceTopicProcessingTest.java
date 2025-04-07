package com.jin.service.marktagservice.service;

import com.jin.service.marktagservice.kafka.utility.RequestType;
import com.jin.service.marktagservice.model.Attendee;
import com.jin.service.marktagservice.model.Event;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class MarkTagServiceTopicProcessingTest {
    @Mock
    private AttendeeRepository attendeeRepositoryDummy;
    @Mock
    private EventRepository eventRepositoryDummy;
    private MarkTagService markTagServiceDummy;
    private Attendee attendeeDummy;
    private List<Attendee> attendeesDummy;

    @BeforeEach
    void setUp() {
        markTagServiceDummy = new MarkTagService(eventRepositoryDummy, attendeeRepositoryDummy);

        attendeeDummy = new Attendee();
        attendeeDummy.setId(1);
        attendeeDummy.setGlobalId(0);

        attendeesDummy= new ArrayList<>();
        attendeesDummy.add(attendeeDummy);
    }

    @Test
    void handlingAddAttendance() {
        //prerequisite
        String dummyData = "{\"id\":0,\"eMail\":\"email\",\"name\":\"attendeeName\",\"role\":\"attendee\"}";

        //function that is being tested
        markTagServiceDummy.processIncomingAttendeeTopic(dummyData);

        //captured
        ArgumentCaptor<Attendee> attendeeArgumentCaptor = ArgumentCaptor.forClass(Attendee.class);
        Mockito.verify(attendeeRepositoryDummy).save(attendeeArgumentCaptor.capture());
        Attendee capturedAttendeeEvent = attendeeArgumentCaptor.getValue();

        //verifying
        int expectedAttendeeGlobalID = 0;
        int actualAttendeeGlobalID = capturedAttendeeEvent.getGlobalId();
        assertEquals(expectedAttendeeGlobalID, actualAttendeeGlobalID);
    }

//    @Test
//    void handlingRemoveAttendance() {
//        //prerequisite
//        String dummyData = "{\"id\":0,\"eMail\":\"email\",\"name\":\"attendeeName\",\"role\":\"attendee\"}";
//        doReturn(Optional.of(attendeeDummy)).when(attendeeRepositoryDummy).findByGlobalId(0);
//
//        //function that is being tested
//        markTagServiceDummy.processIncomingAttendeeTopic(dummyData);
//
//        //captured
//        ArgumentCaptor<Attendee> attendeeArgumentCaptor = ArgumentCaptor.forClass(Attendee.class);
//        Mockito.verify(attendeeRepositoryDummy).delete(attendeeArgumentCaptor.capture());
//        Attendee capturedAttendeeEvent = attendeeArgumentCaptor.getValue();
//
//        //verifying
//        int expectedAttendeeGlobalID = 0;
//        int actualAttendeeGlobalID = capturedAttendeeEvent.getGlobalId();
//        assertEquals(expectedAttendeeGlobalID, actualAttendeeGlobalID);
//    }

    @Test
    void handlingAddEvent() {
        //prerequisite
        String dummyData = "{\"id\":1,\"organizerId\":1,\"name\":\"event\",\"capacity\":1,\"startDate\":\"2023/5/18\",\"endDate\":\"2023/5/18\",\"active\":\"true\"}";
        doReturn(attendeesDummy).when(attendeeRepositoryDummy).findAll();

        Event dummyEvent = new Event();
        //dummyEvent.setId(1);
        dummyEvent.setGlobalId(1);
        dummyEvent.setAttendee(attendeeDummy);

        //function that is being tested
        markTagServiceDummy.processIncomingEventTopic(dummyData);

        //captured
        ArgumentCaptor<Attendee> attendeeArgumentCaptor = ArgumentCaptor.forClass(Attendee.class);
        Mockito.verify(attendeeRepositoryDummy).save(attendeeArgumentCaptor.capture());
        Attendee capturedAttendee = attendeeArgumentCaptor.getValue();

        //verifying
        int expectedAddedEventGID = dummyEvent.getGlobalId();
        int actualAddedEventGID = capturedAttendee.getEvents().get(0).getGlobalId();
        assertEquals(expectedAddedEventGID, actualAddedEventGID);
    }

//    @Test
//    void handlingRemoveEvent() {
//        //prerequisite
//        String dummyData = "{\"eventId\":1,\"organizerId\":1,\"name\":\"event\",\"capacity\":1,\"startDate\":\"2023/5/18\",\"endDate\":\"2023/5/18\",\"active\":\"false\"}";
//        List<Event> events = new ArrayList<>();
//        Event dummyEvent = new Event();
//        dummyEvent.setGlobalId(1);
//        dummyEvent.setAttendee(attendeeDummy);
//        events.add(dummyEvent);
//        attendeeDummy.setEvents(events);
//        doReturn(attendeesDummy).when(attendeeRepositoryDummy).findAll();
//
//        //function that is being tested
//        markTagServiceDummy.processIncomingEventTopic(dummyData);
//
//        //captured
//        ArgumentCaptor<Attendee> attendeeArgumentCaptor = ArgumentCaptor.forClass(Attendee.class);
//        Mockito.verify(attendeeRepositoryDummy).save(attendeeArgumentCaptor.capture());
//        Attendee capturedAttendee = attendeeArgumentCaptor.getValue();
//
//        //verifying
//        int expectedEventsFromAttendee = 0;
//        int actualEventsFromAttendee = capturedAttendee.getEvents().size();
//        assertEquals(expectedEventsFromAttendee, actualEventsFromAttendee);
//
//    }


}
