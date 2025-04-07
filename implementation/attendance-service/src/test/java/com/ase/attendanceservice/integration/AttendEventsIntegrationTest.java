package com.ase.attendanceservice.integration;

import com.ase.attendanceservice.model.Attendee;
import com.ase.attendanceservice.model.Event;
import com.ase.attendanceservice.model.EventBooking;
import com.ase.attendanceservice.notifications.EventBookingNotification;
import com.ase.attendanceservice.producer.StreamProducer;
import com.ase.attendanceservice.repository.AttendeeRepository;
import com.ase.attendanceservice.repository.EventBookingRepository;
import com.ase.attendanceservice.repository.EventRepository;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext
public class AttendEventsIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AttendeeRepository attendeeRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventBookingRepository eventBookingRepository;

    @MockBean
    private StreamProducer streamProducer;

    private static final Long ATTENDEE_ID = 1L;
    private static final Long EVENT_ID = 2L;


    @BeforeAll
    public void setup() {
        Event testEvent = new Event(EVENT_ID, 15l, "name", 200,
                Instant.now().plus(Duration.ofDays(3)), Instant.now().plus(Duration.ofDays(5)));
        Attendee testAttendee = new Attendee(ATTENDEE_ID, "firstname", "lastname", "email");

        attendeeRepository.saveAndFlush(testAttendee);
        eventRepository.saveAndFlush(testEvent);
    }

    @Test
    @Order(1)
    public void executeEventBooking_ShouldCreateAndPublishBooking() throws Exception {
        mockMvc.perform(post("/attendees/1/event-bookings")
                        .content("{\"eventId\": 2}")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id.eventId").value(EVENT_ID))
                .andExpect(jsonPath("$.id.attendeeId").value(ATTENDEE_ID));

        // Check if EventBooking was created in the Database
        Optional<EventBooking> createdBooking = eventBookingRepository.findByAttendeeIdAndEventId(ATTENDEE_ID, EVENT_ID);
        Assertions.assertTrue(createdBooking.isPresent());

        // Check if the producer has been called to publish the data on the broker
        Mockito.verify(streamProducer, Mockito.times(1))
                .publishAttendanceEntry(ArgumentMatchers.any(EventBookingNotification.class));
    }

    @Test
    @Order(2)
    public void getEventBookingsByAttendee_ShouldReturnOneBooking() throws Exception {
        mockMvc.perform(get("/attendees/1/event-bookings")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1)) // Only one event should be returned
                .andExpect(jsonPath("$.[0].attendeeId").value(ATTENDEE_ID));
    }

    @Test
    @Order(3)
    public void getEventBookingsByNonExistentAttendee_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/attendees/2/event-bookings")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(4)
    public void getAttendeesByEvent_ShouldReturnOneAttendee() throws Exception {
        mockMvc.perform(get("/events/2/attendees")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$.[0].id").value(ATTENDEE_ID));
    }

    @Test
    @Order(5)
    public void cancelEventBooking_ShouldDeleteAndPublishBooking() throws Exception {
        mockMvc.perform(delete("/attendees/1/event-bookings?eventId=2")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNoContent());

        Optional<EventBooking> deletedBooking = eventBookingRepository.findByAttendeeIdAndEventId(ATTENDEE_ID, EVENT_ID);
        Assertions.assertTrue(deletedBooking.isEmpty());
    }

    @AfterAll
    public void teardown() {
        eventBookingRepository.deleteAll();
        attendeeRepository.deleteAll();
        eventRepository.deleteAll();
    }
}
