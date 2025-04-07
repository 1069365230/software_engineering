package com.ase.recommenderservice.integration;


import com.ase.recommenderservice.consumer.StreamConsumer;
import com.ase.recommenderservice.model.Attendee;
import com.ase.recommenderservice.model.Event;
import com.ase.recommenderservice.model.EventCategory;
import com.ase.recommenderservice.model.dto.incoming.EventDTO;
import com.ase.recommenderservice.repository.AttendeeRepository;
import com.ase.recommenderservice.repository.EventRepository;
import com.ase.recommenderservice.service.event_notification.EmailSender;
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

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext
public class NewEventNotificationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AttendeeRepository attendeeRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private StreamConsumer streamConsumer;

    @MockBean
    private EmailSender emailSender;

    private Attendee testAttendee;

    private static final String CATEGORY = "Category";

    @BeforeAll
    public void setup() {
        testAttendee = new Attendee(1l, "Austria", "Vienna", "email");
        Event attendingEvent = new Event(2l, new EventCategory(CATEGORY), "Austria", "Vienna",
                "EventName", 200, Instant.now().plus(Duration.ofDays(1)),
                Instant.now().plus(Duration.ofDays(2)));
        testAttendee.getAttendingEvents().add(attendingEvent);
        testAttendee.getPrimaryInterests().put(new EventCategory(CATEGORY), 1.0);

        eventRepository.saveAndFlush(attendingEvent);
        attendeeRepository.saveAndFlush(testAttendee);
    }


    @Test
    @Order(1)
    public void addEventWhichMatchesAttendeeInterest_ShouldNotifyAttendee() {
        // add new event with same city as attendee and previously attended event-category
        streamConsumer.consumeNewEventEntry().accept(new EventDTO(3l, "name", CATEGORY, "Austria", "Vienna",
                100, Instant.now().plus(Duration.ofDays(1)),
                Instant.now().plus(Duration.ofDays(2))));

        List<String> recipientEmails = Arrays.asList(testAttendee.getEmail());
        Mockito.verify(emailSender, Mockito.times(1))
                .sendEmails(eq(recipientEmails), anyString(), anyString());
    }

    @Test
    @Order(2)
    public void addEventWhichMatchesAttendeeInterest_WhenAttendeeUnsubscribesFromNotifications_ShouldNotNotifyAttendee() throws Exception {
        // Unsubscribe from EMail notifications
        mockMvc.perform(patch("/attendees/1/preferences")
                        .content("{\"receivePromotionalEmails\": false}")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNoContent());

        // add new event with same city as attendee and previously attended event-category
        streamConsumer.consumeNewEventEntry().accept(new EventDTO(4l, "name", CATEGORY, "Austria", "Vienna",
                100, Instant.now().plus(Duration.ofDays(1)),
                Instant.now().plus(Duration.ofDays(2))));

        // Attendee should not be notified
        Mockito.verify(emailSender, Mockito.times(0))
                .sendEmails(anyList(), anyString(), anyString());
    }


    @AfterAll
    public void teardown() {
        attendeeRepository.deleteAll();
        eventRepository.deleteAll();
    }
}