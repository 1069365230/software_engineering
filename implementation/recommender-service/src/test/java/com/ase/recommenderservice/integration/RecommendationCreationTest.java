package com.ase.recommenderservice.integration;


import com.ase.recommenderservice.model.Attendee;
import com.ase.recommenderservice.model.Event;
import com.ase.recommenderservice.model.EventCategory;
import com.ase.recommenderservice.repository.AttendeeRepository;
import com.ase.recommenderservice.repository.EventRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext
public class RecommendationCreationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AttendeeRepository attendeeRepository;

    @Autowired
    private EventRepository eventRepository;

    private Attendee testAttendee;
    private Event testEvent;
    private static final Long ATTENDEE_ID = 1L;
    private static final Long EVENT_ID = 2L;

    @BeforeAll
    public void setup() {
        testEvent = new Event(EVENT_ID, new EventCategory("Category"), "Country", "City",
                "EventName", 200, Instant.now().plus(Duration.ofDays(1)),
                Instant.now().plus(Duration.ofDays(2)));
        testAttendee = new Attendee(ATTENDEE_ID, "Country", "City", "email");

        attendeeRepository.saveAndFlush(testAttendee);
        eventRepository.saveAndFlush(testEvent);
    }

    @Test
    @Order(1)
    public void getEventRecommendation_ShouldReturnCorrectRecommendation() throws Exception {
        mockMvc.perform(get("/attendees/1/recommendations")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1)) // Only 1 Event is available
                .andExpect(jsonPath("$.[0].eventId").value(EVENT_ID))
                .andExpect(jsonPath("$.[0].relevanceScore").value("100.00%"));
    }


    @Test
    @Order(2)
    public void getEventRecommendation_WhenAlreadyAttendingEvent_ShouldReturn404() throws Exception {
        // Attend the current event, which should remove it from the recommendations
        testAttendee.getAttendingEvents().add(testEvent);
        attendeeRepository.save(testAttendee);

        mockMvc.perform(get("/attendees/1/recommendations")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(3)
    public void getEventRecommendation_WhenAttendeeDoesNotExist_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/attendees/15/recommendations")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound());
    }

    @AfterAll
    public void teardown() {
        attendeeRepository.deleteAll();
        eventRepository.deleteAll();
    }
}
