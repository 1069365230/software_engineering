package at.univie.davidreichert.analyticsandreport.integration;


import at.univie.davidreichert.analyticsandreport.model.*;
import at.univie.davidreichert.analyticsandreport.repo.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext
public class ReportsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventBookmarkRepository eventBookmarkRepository;

    @Autowired
    private AttendanceEntryRepository attendanceEntryRepository;

    @BeforeAll
    public void setup() {

        // Setup Test Event
        Event testEvent = new Event();
        testEvent.setName("Integration Test Event");
        testEvent.setId(1L);
        testEvent.setStartDate(Instant.now().plus(2, ChronoUnit.DAYS));
        testEvent.setEndDate(Instant.now().plus(5, ChronoUnit.DAYS));
        testEvent.setOrganizerId(50L);

        eventRepository.save(testEvent);

        // Setup Test Bookmarks

        for (int i = 1; i <= 45; i++) {
            EventBookmark bookmark = new EventBookmark();
            bookmark.setEventId(1L);
            bookmark.setAttendeeId((long) i);
            bookmark.setAction(true);
            bookmark.setBookmarkId(i);
            eventBookmarkRepository.save(bookmark);
        }

        // Setup test AttendanceEntries

        for (int i = 1; i <= 75; i++) {
            AttendanceEntry attendanceEntry = new AttendanceEntry();
            attendanceEntry.setEventId(1L);
            attendanceEntry.setAttendeeId((long)i);
            attendanceEntry.setActive(true);
            attendanceEntry.setAttendanceId((long)i);
            attendanceEntryRepository.save(attendanceEntry);
        }

        // Setup Test Feedbacks

        for (int i = 1; i <= 10; i++) {
            Feedback feedback = new Feedback();
            feedback.setEventId(1L);
            feedback.setFeedbackId((long)i);
            feedback.setAttendeeId((long)i);
            feedback.setOverallrating(5);
            feedback.setDescriptionRating(5);
            feedback.setLocationRating(5);
            feedbackRepository.save(feedback);

        }


    }

    @Test
    @Order(1)
    public void requestReport_AssertDataAccessable() throws Exception {

        mockMvc.perform(get("/reports/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numberOfAttendees").value(75))
                .andExpect(jsonPath("$.numberOfBookmarks").value((45)))
                .andExpect(jsonPath("$.numberOfFeedbacks").value(10));
    }

    @Test
    @Order(2)
    public void attendeesAdded_AssertReportUpdated() throws Exception {

        for (int i = 76; i <= 150; i++) {
            AttendanceEntry attendanceEntry = new AttendanceEntry();
            attendanceEntry.setEventId(1L);
            attendanceEntry.setAttendeeId((long)i);
            attendanceEntry.setActive(true);
            attendanceEntry.setAttendanceId((long)i);
            attendanceEntryRepository.save(attendanceEntry);
        }


        mockMvc.perform(get("/reports/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numberOfAttendees").value(150))
                .andExpect(jsonPath("$.numberOfBookmarks").value((45)))
                .andExpect(jsonPath("$.numberOfFeedbacks").value(10));
    }


    @Test
    @Order(3)
    public void requestReportForNotExistingEvent_AssertNotFound() throws Exception {
        mockMvc.perform(get("/reports/3"))
                .andExpect(status().isNotFound());
    }

    @AfterAll
    public void cleanup() {
        attendanceEntryRepository.deleteAll();
        feedbackRepository.deleteAll();
        eventBookmarkRepository.deleteAll();
        eventRepository.deleteAll();
    }


}
