package com.ase.attendanceservice.integration;

import com.ase.attendanceservice.constants.MessageConstants;
import com.ase.attendanceservice.model.*;
import com.ase.attendanceservice.repository.AttendeeRepository;
import com.ase.attendanceservice.repository.EventBookingRepository;
import com.ase.attendanceservice.repository.EventRepository;
import com.ase.attendanceservice.service.EmailSender;
import org.junit.jupiter.api.*;
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
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext
public class OrganizerMessagingIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AttendeeRepository attendeeRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventBookingRepository eventBookingRepository;

    @MockBean
    private EmailSender emailSender;

    private static final Long ATTENDEE_ID = 1L;
    private static final String ATTENDEE_EMAIL = "email@example.com";
    private static final Long EVENT_ID = 2L;
    private static final Long ORGANIZER_ID = 3L;

    @BeforeAll
    public void setup() {
        Event testEvent = new Event(EVENT_ID, ORGANIZER_ID, "name", 200,
                Instant.now().plus(Duration.ofDays(3)), Instant.now().plus(Duration.ofDays(5)));

        Attendee testAttendee = new Attendee(ATTENDEE_ID, "firstname", "lastname", ATTENDEE_EMAIL);
        Attendee testAttendeeNotAttending = new Attendee(2L, "firstname2", "lastname2", "email2");

        Ticket testTicket = new Ticket(testEvent, testAttendee, TicketType.GeneralAdmission,
                Instant.now(), Instant.now(), new byte[0]);
        EventBooking eventBooking = new EventBooking(testAttendee, testEvent, testTicket, Instant.now());

        attendeeRepository.saveAllAndFlush(Arrays.asList(testAttendee, testAttendeeNotAttending));
        eventRepository.saveAndFlush(testEvent);
        eventBookingRepository.saveAndFlush(eventBooking);
    }

    @Test
    @Order(1)
    public void organizerOfAnotherEvent_ShouldBeUnauthorized() throws Exception {
        mockMvc.perform(post("/events/2/messages")
                        .content("{" +
                                "    \"organizerId\" : 300," + // Wrong OrganizerID
                                "    \"message\" : \"exampleMessage\"," +
                                "    \"recipientIds\":[" +
                                "        1" +
                                "    ]" +
                                "}")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(2)
    public void organizerMessageSent_ShouldBeForwardedToAttendees() throws Exception {
        final String MESSAGE = "exampleMessage";
        mockMvc.perform(post("/events/2/messages")
                        .content("{" +
                                "    \"organizerId\" : 3," +
                                "    \"message\" :\"" + MESSAGE + "\"," +
                                "    \"recipientIds\":[" +
                                "        1" +
                                "    ]" +
                                "}")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNoContent());

        // Check if EmailSender has been called with the correct parameters
        List<String> recipientEmails = Arrays.asList(ATTENDEE_EMAIL);
        Mockito.verify(emailSender, Mockito.times(1))
                .sendEmails(recipientEmails, MessageConstants.NEW_ORGANIZER_MESSAGE, MESSAGE);
    }

    @Test
    public void getAttendeesByEvent_ShouldReturnCorrectAttendee() throws Exception {
        mockMvc.perform(get("/events/2/attendees")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1)) // Event only has one Attendee
                .andExpect(jsonPath("$.[0].id").value(ATTENDEE_ID));
    }

    @AfterAll
    public void teardown() {
        eventBookingRepository.deleteAll();
        attendeeRepository.deleteAll();
        eventRepository.deleteAll();
    }
}
