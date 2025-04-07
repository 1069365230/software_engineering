package at.univie.davidreichert.feedbackservice.integration;


import at.univie.davidreichert.feedbackservice.model.AttendanceEntry;
import at.univie.davidreichert.feedbackservice.model.Attendee;
import at.univie.davidreichert.feedbackservice.model.Event;
import at.univie.davidreichert.feedbackservice.model.Feedback;
import at.univie.davidreichert.feedbackservice.producer.FeedbackProducer;
import at.univie.davidreichert.feedbackservice.repo.AttendanceEntryRepository;
import at.univie.davidreichert.feedbackservice.repo.AttendeeRepository;
import at.univie.davidreichert.feedbackservice.repo.EventRepository;
import at.univie.davidreichert.feedbackservice.repo.FeedbackRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
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
public class FeedbackIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AttendeeRepository attendeeRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private AttendanceEntryRepository attendanceEntryRepository;

    @MockBean
    private FeedbackProducer feedbackProducer;

    @BeforeAll
    public void setup() {
        Event testEvent = new Event(1L, 101L, "test event",
                Instant.now().plus(Duration.ofDays(2)), Instant.now().plus(Duration.ofDays(5)));
        Attendee testAttendee = new Attendee(1L, "username", "user@test.com", "userForname", "userSurname", "attendee", "Test User");

        AttendanceEntry testAttendance = new AttendanceEntry(1L, 1L, 1L, true);

        attendeeRepository.saveAndFlush(testAttendee);
        eventRepository.saveAndFlush(testEvent);
        attendanceEntryRepository.saveAndFlush(testAttendance);
    }

    @Test
    @Order(1)
    public void createFeedback_AssertCreationAndPublishing() throws Exception {
        Feedback feedback = new Feedback();
        feedback.setEventId(1L);
        feedback.setAttendeeId(1L);
        feedback.setComment("This is a test comment");
        feedback.setLocationrating(5);
        feedback.setDescriptionrating(5);
        feedback.setOverallrating(5);

        ObjectMapper objectMapper = new ObjectMapper();
        String feedbackJson = objectMapper.writeValueAsString(feedback);

        mockMvc.perform(post("/feedbacks/new")
                        .content(feedbackJson)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.feedbackId").exists())
                .andExpect(jsonPath("$.eventId").value(1L))
                .andExpect(jsonPath("$.attendeeId").value(1L));


        Optional<Feedback> createdFeedback = feedbackRepository.findByAttendeeIdAndEventId(1L, 1L);
        Assertions.assertTrue(createdFeedback.isPresent());

        Mockito.verify(feedbackProducer, Mockito.times(1))
                .produceFeedbackEvent(ArgumentMatchers.any(Feedback.class));

    }

    @Test
    @Order(2)
    public void getFeedbacksByAttendee_ShouldReturnSpecificFeedback() throws Exception {
        mockMvc.perform(get("/feedbacks/attendee/1")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].comment").value("This is a test comment"))
                .andExpect(jsonPath("$[0].locationrating").value(5))
                .andExpect(jsonPath("$[0].descriptionrating").value(5))
                .andExpect(jsonPath("$[0].overallrating").value(5))
                .andExpect(jsonPath("$[0].attendeeId").value(1L));
    }

    @Test
    @Order(3)
    public void getAllFeedbacks_ShouldReturnAllFeedbacks() throws Exception {
        mockMvc.perform(get("/feedbacks")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].comment").value("This is a test comment"))
                .andExpect(jsonPath("$[0].locationrating").value(5))
                .andExpect(jsonPath("$[0].descriptionrating").value(5))
                .andExpect(jsonPath("$[0].overallrating").value(5))
                .andExpect(jsonPath("$[0].attendeeId").value(1L));
    }


    @Test
    @Order(4)
    public void addFeedbackByNotAttending_ShouldReturnAttendeeNotAttending() throws Exception {
        Feedback feedback = new Feedback();
        feedback.setEventId(1L);
        feedback.setAttendeeId(2L); // AttendeeId is not in Attendance Entry DB
        feedback.setComment("This is a test comment");
        feedback.setLocationrating(5);
        feedback.setDescriptionrating(5);
        feedback.setOverallrating(5);

        ObjectMapper objectMapper = new ObjectMapper();
        String feedbackJson = objectMapper.writeValueAsString(feedback);

        mockMvc.perform(post("/feedbacks/new")
                        .content(feedbackJson)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest());
    }


    @Test
    @Order(5)
    public void getAllFeedbacks_ShouldReturnNoContentWhenNoFeedbacksExistWereRemoved() throws Exception {
        feedbackRepository.deleteAll();

        mockMvc.perform(get("/feedbacks")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNoContent());
    }

    @AfterAll
    public void cleanup() {
        attendanceEntryRepository.deleteAll();
        attendeeRepository.deleteAll();
        eventRepository.deleteAll();
        feedbackRepository.deleteAll();
    }




}
