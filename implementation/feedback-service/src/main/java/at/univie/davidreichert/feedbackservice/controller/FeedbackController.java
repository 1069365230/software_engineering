package at.univie.davidreichert.feedbackservice.controller;

import at.univie.davidreichert.feedbackservice.exceptions.AttendeeNotAttendingException;
import at.univie.davidreichert.feedbackservice.exceptions.EventNotFoundException;
import at.univie.davidreichert.feedbackservice.exceptions.FeedbackNotFoundException;
import at.univie.davidreichert.feedbackservice.model.Feedback;
import at.univie.davidreichert.feedbackservice.service.FeedbackService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/feedbacks")
public class FeedbackController {


    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    /**
     * HTTP POST request: Add a new feedback or update an existing one.
     *
     * @param feedback The feedback object to be added.
     * @return ResponseEntity with the added feedback if successful, bad request if attendee is not attending or event is not found.
     */
    @PostMapping(path = "/new")
    public ResponseEntity<Feedback> addFeedback(@RequestBody Feedback feedback) {
        try {
            Feedback newFeedback = feedbackService.addFeedback(feedback);
            return ResponseEntity.ok(newFeedback);
        } catch (AttendeeNotAttendingException e) {
            return ResponseEntity.badRequest().build();
        } catch (EventNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * HTTP GET request: Get the health status of the feedback API.
     *
     * @return HttpStatus OK.
     */
    @GetMapping("/health")
    public HttpStatus getHealth() {
        return HttpStatus.OK;
    }

    /**
     * HTTP GET request: Get all existing feedbacks.
     *
     * @return ResponseEntity with the list of feedbacks if available, no content if no feedbacks are found.
     */
    @GetMapping("")
    public ResponseEntity<List<Feedback>> getAllFeedbacks() {
        List<Feedback> feedbacks = feedbackService.getAllFeedbacks();
        if (feedbacks.size() == 0) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(feedbacks);
        }
    }

    /**
     * HTTP GET request: Get feedback by feedback ID.
     *
     * @param feedbackId The ID of the feedback to retrieve.
     * @return ResponseEntity with the feedback if found, no content if feedback is not found.
     */
    @GetMapping(path = "/{feedbackId}")
    public ResponseEntity<Feedback> getFeedbackById(@PathVariable Long feedbackId) {
    try {
        Feedback feedback = feedbackService.getFeedbackByFeedbackId(feedbackId);
        return ResponseEntity.ok(feedback);
    } catch (FeedbackNotFoundException e) {
        return ResponseEntity.noContent().build(); // No Feedbacks found - 204 returned
    }
}

    /**
     * HTTP GET request: Get feedbacks by attendee ID.
     *
     * @param attendeeId The ID of the attendee to retrieve feedbacks for.
     * @return ResponseEntity with the list of feedbacks if available, no content if no feedbacks are found.
     */
    @GetMapping("/attendee/{attendeeId}")
    public ResponseEntity<List<Feedback>> findFeedbacksByAttendeeId(@PathVariable Long attendeeId) {
        try {
            List<Feedback> feedbacks = feedbackService.findFeedbacksByAttendeeId(attendeeId);
            return ResponseEntity.ok(feedbacks);
        } catch (FeedbackNotFoundException e) {
            return ResponseEntity.noContent().build(); // No Feedbacks found - 204 returned
        }
    }

    /**
     * HTTP GET request: Get feedbacks by event ID.
     *
     * @param eventId The ID of the event to retrieve feedbacks for.
     * @return ResponseEntity with the list of feedbacks if available, no content if no feedbacks are found.
     */
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<Feedback>> findFeedbacksByEventId(@PathVariable Long eventId) {
        try {
            List<Feedback> feedbacks = feedbackService.findFeedbacksByEventId(eventId);
            return ResponseEntity.ok(feedbacks);
        } catch (FeedbackNotFoundException e) {
            return ResponseEntity.noContent().build(); // No Feedbacks found - 204 returned
        }
    }


    //TODO: Das muss auf HTTP DEL geändert werden
    /**
     * HTTP GET request: Delete feedback by attendee ID and event ID.
     *
     * @param attendeeId The ID of the attendee associated with the feedback.
     * @param eventId The ID of the event associated with the feedback.
     * @return ResponseEntity with no body if feedback is found and deleted, no content if feedback is not found.
     */
    @GetMapping(path = "/delete/{attendeeId}/{eventId}")
    public ResponseEntity<Void> deleteFeedbackByAttendeeIdAndEventId(@PathVariable Long attendeeId, @PathVariable Long eventId) {
        try {
            feedbackService.deleteFeedbackByAttendeeIdAndEventId(attendeeId, eventId);
            return ResponseEntity.ok().build(); // Feedback found and deleted - 204 returned
        } catch (FeedbackNotFoundException e) {
            return ResponseEntity.noContent().build(); // Feedback not found - 204 returned
        }
    }

    /**
    *
     * Retrieve the feedback by attendee ID and event ID.
     * @param attendeeId The ID of the attendee.
     * @param eventId The ID of the event.
     * @return ResponseEntity containing the feedback if found (HTTP 200), or an empty response if not found (HTTP 204).
     */
    @GetMapping(path = "/attendee/{attendeeId}/event/{eventId}")
    public ResponseEntity<Feedback> getFeedbackByAttendeeIdAndEventId(@PathVariable Long attendeeId, @PathVariable Long eventId) {
        try {
            Feedback feedback = feedbackService.getFeedbackByAttendeeIdAndEventId(attendeeId, eventId);
            return ResponseEntity.ok(feedback); // Feedback found and returned - 200 returned
        } catch (FeedbackNotFoundException e) {
            return ResponseEntity.noContent().build(); // Feedback not found - 204 returned
        }
    }


    /**
     * HTTP GET request: Delete feedback by feedback ID.
     *
     * @param feedbackId The ID of the feedback to be deleted.
     * @return ResponseEntity with no body if feedback is found and deleted, no content if feedback is not found.
     */
    @GetMapping(path = "/delete/id/{feedbackId}")
    public ResponseEntity<Void> deleteFeedbackByFeedbackId(@PathVariable Long feedbackId) {
        try {
            feedbackService.deleteFeedbackByFeedbackId(feedbackId);
            return ResponseEntity.ok().build(); // Feedback found and deleted - 204 returned
        } catch (FeedbackNotFoundException e) {
            return ResponseEntity.noContent().build(); // Feedback not found - 404 returned
        }
    }

    /**
     * HTTP GET request: Delete all feedbacks by attendee ID.
     *
     * @param attendeeId The ID of the attendee to delete all feedbacks for.
     * @return ResponseEntity with no body if feedbacks are found and deleted, no content if no feedbacks are found.
     */
    @GetMapping(path = "/delete/attendee/{attendeeId}")
    public ResponseEntity<Void> deleteAllFeedbacksByAttendeeId(@PathVariable Long attendeeId) {
        try {
            feedbackService.deleteAllFeedbacksByAttendeeId(attendeeId);
            return ResponseEntity.ok().build(); // Feedback found and deleted - 204 returned
        } catch (FeedbackNotFoundException e) {
            return ResponseEntity.noContent().build(); // Feedback not found - 404 returned
        }
    }

    /**
     * HTTP GET request: Delete all feedbacks by event ID.
     *
     * @param eventId The ID of the event to delete all feedbacks for.
     * @return ResponseEntity with no body if feedbacks are found and deleted, no content if no feedbacks are found.
     */
    @GetMapping(path = "/delete/event/{eventId}")
    public ResponseEntity<Void> deleteAllFeedbacksByEventId(@PathVariable Long eventId) {
        try {
            feedbackService.deleteAllFeedbacksByEventId(eventId);
            return ResponseEntity.ok().build(); // Feedback found and deleted - 204 returned
        } catch (FeedbackNotFoundException e) {
            return ResponseEntity.noContent().build(); // Feedback not found - 404 returned
        }
    }

}

