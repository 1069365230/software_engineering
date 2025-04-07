package at.univie.davidreichert.feedbackservice.unit;

import at.univie.davidreichert.feedbackservice.FeedbackserviceApplication;
import at.univie.davidreichert.feedbackservice.TestConfig;
import at.univie.davidreichert.feedbackservice.exceptions.AttendeeNotAttendingException;
import at.univie.davidreichert.feedbackservice.exceptions.FeedbackNotFoundException;
import at.univie.davidreichert.feedbackservice.model.Feedback;
import at.univie.davidreichert.feedbackservice.producer.FeedbackProducer;
import at.univie.davidreichert.feedbackservice.repo.FeedbackRepository;
import at.univie.davidreichert.feedbackservice.service.AttendanceEntryService;
import at.univie.davidreichert.feedbackservice.service.AttendeeService;
import at.univie.davidreichert.feedbackservice.service.EventService;
import at.univie.davidreichert.feedbackservice.service.FeedbackService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@Import(TestConfig.class)
@DataJpaTest(excludeAutoConfiguration = FeedbackserviceApplication.class)
public class FeedbackServiceTest {


    @MockBean
    private AttendanceEntryService attendanceEntryService;

    @MockBean
    private AttendeeService attendeeService;

    @MockBean
    private FeedbackProducer feedbackProducer;

    @MockBean
    private FeedbackRepository feedbackRepository;

    @MockBean
    private EventService eventService;

    private FeedbackService feedbackService;




    @BeforeEach
    public void setup() {
        feedbackService = new FeedbackService(attendanceEntryService, feedbackRepository, feedbackProducer, attendeeService);
    }

    /**
     * Exception handling: Test case to verify that adding feedback when the attendee
     * is not attending the event throws the according exception.
     */

    @Test
    public void addFeedback_notAttending_throwsException() {
        Feedback feedback = new Feedback();
        feedback.setAttendeeId(1L);
        feedback.setEventId(1L);

        when(attendanceEntryService.isAttending(feedback.getAttendeeId(), feedback.getEventId())).thenReturn(false);

        Exception exception = assertThrows(AttendeeNotAttendingException.class, () ->
                feedbackService.addFeedback(feedback));

        assertEquals("Attendee with ID " + feedback.getAttendeeId()
                + " is not attending Event with ID " + feedback.getEventId(), exception.getMessage());
    }

    /**
     * Test case to verify that a new feedback is saved successfully.
     */
    @Test
    public void addFeedback_newFeedback_savedSuccessfully() {
        Feedback feedback = new Feedback();
        feedback.setAttendeeId(1L);
        feedback.setEventId(1L);

        when(attendanceEntryService.isAttending(feedback.getAttendeeId(), feedback.getEventId())).thenReturn(true);
        when(feedbackRepository.findByAttendeeIdAndEventId(feedback.getAttendeeId(), feedback.getEventId())).thenReturn(Optional.empty());
        when(attendeeService.findNameByAttendeeId(feedback.getAttendeeId())).thenReturn("David Reichert");
        when(feedbackRepository.save(any(Feedback.class))).thenAnswer(i -> i.getArguments()[0]);

        Feedback result = feedbackService.addFeedback(feedback);

        assertEquals("David Reichert", result.getAttendeeName());
    }

    /**
     * Test case to verify that an existing feedback is updated successfully.
     */
    @Test
    public void addFeedback_existingFeedback_updatedSuccessfully() {
        Feedback existingFeedback = new Feedback();
        existingFeedback.setAttendeeId(1L);
        existingFeedback.setEventId(1L);
        existingFeedback.setComment("Old feedback comment");

        Feedback newFeedback = new Feedback();
        newFeedback.setAttendeeId(1L);
        newFeedback.setEventId(1L);
        newFeedback.setComment("New feedback comment");

        when(attendanceEntryService.isAttending(newFeedback.getAttendeeId(), newFeedback.getEventId())).thenReturn(true);
        when(feedbackRepository.findByAttendeeIdAndEventId(newFeedback.getAttendeeId(), newFeedback.getEventId())).thenReturn(Optional.of(existingFeedback));
        when(feedbackRepository.save(any(Feedback.class))).thenAnswer(i -> i.getArguments()[0]);

        Feedback result = feedbackService.addFeedback(newFeedback);

        assertEquals("New feedback comment", result.getComment());
    }

    /**
     * Test case to verify the deletion of a feedback by its feedback ID.
     */
    @Test
    public void testDeleteFeedback() {
        Feedback feedback = createFeedback(1L, 1L, "David Reichert", "Super duper nice.", 4, 5, 4);
        when(feedbackRepository.findByFeedbackId(1L)).thenReturn(Optional.of(feedback));

        feedbackService.deleteFeedbackByFeedbackId(1L);

        ArgumentCaptor<Feedback> captor = ArgumentCaptor.forClass(Feedback.class);
        verify(feedbackRepository).delete(captor.capture());
        verify(feedbackRepository).findByFeedbackId(1L);
        assertEquals(feedback, captor.getValue());
        verifyNoMoreInteractions(feedbackRepository);
    }

    /**
     * Test case to verify the retrieval of all feedbacks.
     */
    @Test
    public void testGetAllFeedbacks() {
        List<Feedback> feedbackList = new ArrayList<>();
        Feedback feedback = createFeedback(1L, 1L, "David Reichert", "Super duper nice.", 4, 5, 4);
        feedbackList.add(createFeedback(2L, 2L, "Philipp Reichert", "Horrible.", 5, 5, 5));

        when(feedbackRepository.findAll()).thenReturn(feedbackList);

        List<Feedback> result = feedbackService.getAllFeedbacks();

        assertEquals(feedbackList, result);

        verify(feedbackRepository).findAll();
        verifyNoMoreInteractions(feedbackRepository);
    }

    /**
     * Test case to verify the retrieval of a feedback by its ID.
     */
    @Test
    public void testGetFeedbackById() {
        Long attendeeId = 1L;
        Long eventId = 1L;

        when(attendanceEntryService.isAttending(attendeeId, eventId)).thenReturn(true);

        Feedback feedback = new Feedback();
        feedback.setAttendeeId(attendeeId);
        feedback.setEventId(eventId);
        feedback.setComment("Super duper nice.");

        when(feedbackRepository.findByFeedbackId(1L)).thenReturn(Optional.of(feedback));
        when(feedbackRepository.save(feedback)).thenReturn(feedback);

        Feedback result = feedbackService.getFeedbackByFeedbackId(1L);

        assertEquals(feedback, result);

        verify(feedbackRepository).findByFeedbackId(1L);
        verifyNoMoreInteractions(feedbackRepository);
    }

    /**
     * Test case to verify successful deletion of feedback by attendee ID and event ID.
     */
    @Test
    public void deleteFeedbackByAttendeeIdAndEventId_Success() {
        Feedback feedback = createFeedback(1L, 1L, "David Reichert", "Super duper nice.", 4, 5, 4);
        when(feedbackRepository.findByAttendeeIdAndEventId(1L, 1L)).thenReturn(Optional.of(feedback));

        feedbackService.deleteFeedbackByAttendeeIdAndEventId(1L, 1L);

        verify(feedbackRepository).delete(feedback);
        verify(feedbackProducer).produceFeedbackEvent(feedback);
    }

    /**
     * Exception handling: Test case to verify failure in deletion of feedback by
     * attendee ID and event ID when the feedback does not exist.
     */
    @Test
    public void deleteFeedbackByAttendeeIdAndEventId_Failure() {
        when(feedbackRepository.findByAttendeeIdAndEventId(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(FeedbackNotFoundException.class, () -> feedbackService.deleteFeedbackByAttendeeIdAndEventId(1L, 1L));
    }

    /**
     * Test case to verify successful retrieval of feedbacks by event ID.
     */
    @Test
    public void findFeedbacksByEventId_Success() {
        Feedback feedback = createFeedback(1L, 1L, "David Reichert", "Super duper nice.", 4, 5, 4);
        List<Feedback> feedbackList = Arrays.asList(feedback);
        when(feedbackRepository.findFeedbackByEventId(1L)).thenReturn(Optional.of(feedbackList));

        List<Feedback> result = feedbackService.findFeedbacksByEventId(1L);

        assertEquals(feedbackList, result);
    }

    /**
     * Exception handling: Test case to verify failure in retrieval of feedbacks by event ID when no feedbacks are found.
     */
    @Test
    public void findFeedbacksByEventId_Failure() {
        when(feedbackRepository.findFeedbackByEventId(1L)).thenReturn(Optional.empty());

        assertThrows(FeedbackNotFoundException.class, () -> feedbackService.findFeedbacksByEventId(1L));
    }

    /**
     * Test case to verify successful retrieval of feedback by attendee ID and event ID.
     */
    @Test
    public void getFeedbackByAttendeeIdAndEventId_Success() {
        Feedback feedback = createFeedback(1L, 1L, "David Reichert", "Super duper nice.", 4, 5, 4);
        when(feedbackRepository.findByAttendeeIdAndEventId(1L, 1L)).thenReturn(Optional.of(feedback));

        Feedback result = feedbackService.getFeedbackByAttendeeIdAndEventId(1L, 1L);

        assertEquals(feedback, result);
    }
    /**
     * Exception handling: Test case to verify failure in retrieval
     * of feedback by attendee ID and event ID when the feedback does not exist.
     */
    @Test
    public void getFeedbackByAttendeeIdAndEventId_Failure() {
        when(feedbackRepository.findByAttendeeIdAndEventId(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(FeedbackNotFoundException.class, () -> feedbackService.getFeedbackByAttendeeIdAndEventId(1L, 1L));
    }

    /**
     * Utility method: Creation of feedback instances for testing
     */
    private Feedback createFeedback(Long attendeeId, Long eventId, String attendeeName, String comment,
                                    int locationRating, int descriptionRating, int overallRating) {
        Feedback feedback = new Feedback();
        feedback.setAttendeeId(attendeeId);
        feedback.setEventId(eventId);
        feedback.setAttendeeName(attendeeName);
        feedback.setComment(comment);
        feedback.setLocationrating(locationRating);
        feedback.setDescriptionrating(descriptionRating);
        feedback.setOverallrating(overallRating);
        return feedback;
    }




}
