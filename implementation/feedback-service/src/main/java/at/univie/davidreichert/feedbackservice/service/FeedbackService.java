package at.univie.davidreichert.feedbackservice.service;

import at.univie.davidreichert.feedbackservice.consumer.AttendeeConsumer;
import at.univie.davidreichert.feedbackservice.exceptions.AttendeeNotAttendingException;
import at.univie.davidreichert.feedbackservice.exceptions.FeedbackNotFoundException;
import at.univie.davidreichert.feedbackservice.model.Feedback;
import at.univie.davidreichert.feedbackservice.producer.FeedbackProducer;
import at.univie.davidreichert.feedbackservice.repo.FeedbackRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FeedbackService {

    private static final Logger logger = LoggerFactory.getLogger(AttendeeConsumer.class);

    private final FeedbackRepository feedbackRepository;
    private final AttendanceEntryService attendanceEntryService;
    private final FeedbackProducer feedbackProducer;

    private final AttendeeService attendeeService;

    @Autowired
    public FeedbackService(AttendanceEntryService attendanceEntryService, FeedbackRepository feedbackRepository, FeedbackProducer feedbackProducer, AttendeeService attendeeService) {
        this.attendanceEntryService = attendanceEntryService;
        this.feedbackRepository = feedbackRepository;
        this.feedbackProducer = feedbackProducer;
        this.attendeeService = attendeeService;
    }

    /**
     * Adds or updates feedback provided by an attendee for an event.
     * If the attendee is not actively attending the event, throws AttendeeNotAttendingException.
     * If a feedback entry from the same attendee for the same event already exists, updates that feedback entry.
     * Otherwise, creates a new feedback entry.
     * After adding or updating the feedback, produces a feedback event.
     *
     * @param feedback The feedback provided by the attendee.
     * @return The saved feedback entry.
     * @throws AttendeeNotAttendingException if the attendee is not actively attending the event.
     * @throws IllegalArgumentException if the feedback is null, or if the attendee id or event id does not exist.
     */
    public Feedback addFeedback(Feedback feedback) {
        // Attendee is not actively (AttendanceEntry active=false) attending
        if (!attendanceEntryService.isAttending(feedback.getAttendeeId(), feedback.getEventId())) {
            throw new AttendeeNotAttendingException("Attendee with ID " + feedback.getAttendeeId()
                    + " is not attending Event with ID " + feedback.getEventId());
        }
        Optional<Feedback> existingFeedback = feedbackRepository
                .findByAttendeeIdAndEventId(feedback.getAttendeeId(), feedback.getEventId());

        Feedback savedFeedback;
        // An according feedback is already existing
        if (existingFeedback.isPresent()) {
            savedFeedback = updateExistingFeedback(existingFeedback.get(), feedback);
        } else {
            // Get Attendee Name out of Attendee Table by AttendeeID
            feedback.setAttendeeName(attendeeService.findNameByAttendeeId(feedback.getAttendeeId()));
            // Since it's creation of a new feedback, active is true by default
            feedback.setActive(true);
            savedFeedback = feedbackRepository.save(feedback);
        }
        feedbackProducer.produceFeedbackEvent(savedFeedback);

        return savedFeedback;
    }

    /**
     * Updates an existing feedback with new data provided by an attendee.
     * The comment, description rating, location rating and overall rating are updated.
     * After updating the feedback, the feedback entry is saved in the repository.
     *
     * @param existingFeedback The existing feedback entry to be updated.
     * @param newFeedback The new feedback data provided by the attendee.
     * @return The updated and saved feedback entry.
     * @throws IllegalArgumentException if either of the feedback parameters is null.
     */
    private Feedback updateExistingFeedback(Feedback existingFeedback, Feedback newFeedback) {
        existingFeedback.setComment(newFeedback.getComment());
        existingFeedback.setDescriptionrating(newFeedback.getDescriptionrating());
        existingFeedback.setLocationrating(newFeedback.getLocationrating());
        existingFeedback.setOverallrating(newFeedback.getOverallrating());
        return feedbackRepository.save(existingFeedback);
    }

    /**
     * Deletes a feedback entry by given attendee and event IDs.
     *
     * Locates a feedback entry using attendee and event IDs. If found, sets its active status to false and deletes it.
     * A feedback event is produced upon deletion.
     *
     * @param attendeeId The ID of the attendee.
     * @param eventId The ID of the event.
     * @throws FeedbackNotFoundException if no feedback entry matches the provided IDs.
     */
    public void deleteFeedbackByAttendeeIdAndEventId(Long attendeeId, Long eventId) {
        Optional<Feedback> existingFeedback = feedbackRepository.findByAttendeeIdAndEventId(attendeeId, eventId);

        if (existingFeedback.isPresent()) {
            Feedback feedback = existingFeedback.get();
            // Set the Feedbacks active status to false because of Deletion request
            feedback.setActive(false);
            feedbackRepository.delete(feedback);

            feedbackProducer.produceFeedbackEvent(feedback);
        } else {
            throw new FeedbackNotFoundException("Feedback not found for Attendee with ID " + attendeeId +
                    " and Event with ID " + eventId);
        }
    }

    /**
     * Deletes a feedback entry by given feedback ID.
     *
     * Locates a feedback entry using the feedback ID. If found, sets its active status to false and deletes it.
     * The produceFeedbackEvent method is invoked with the removed feedback as parameter.
     *
     * @param feedbackId The ID of the feedback.
     * @throws FeedbackNotFoundException if no feedback entry matches the provided IDs.
     */
    public void deleteFeedbackByFeedbackId(Long feedbackId) {
        try {
        Optional<Feedback> existingFeedback = feedbackRepository.findByFeedbackId(feedbackId);

        if (existingFeedback.isPresent()) {
            Feedback feedback = existingFeedback.get();
            feedback.setActive(false);
            feedbackRepository.delete(feedback);
            feedbackProducer.produceFeedbackEvent(feedback);
        } else {
            throw new FeedbackNotFoundException("Feedback not found with ID " + feedbackId);
        }
    } catch (FeedbackNotFoundException e) {
        logger.info("Feedback not found with ID" + feedbackId);
        throw e;
        }
    }

    /**
     * Deletes all feedbacks associated with a given event ID.
     * Throws an exception if no feedbacks are found.
     *
     * @param eventId the event ID for which all associated feedbacks are to be deleted.
     * @throws FeedbackNotFoundException if there are no feedbacks for the according event ID found.
     */
    public void deleteAllFeedbacksByEventId(Long eventId) {
        try {
            Optional<List<Feedback>> optionalFeedbackList = Optional.ofNullable(findFeedbacksByEventId(eventId));
            if (optionalFeedbackList.isPresent()) {
                List<Feedback> feedbackList = optionalFeedbackList.get();
                for (Feedback feedback : feedbackList) {
                    feedbackRepository.delete(feedback);
                    feedbackProducer.produceFeedbackEvent(feedback);
                }
            } else {
                throw new FeedbackNotFoundException("No Feedbacks found by Event ID: " + eventId);
            }
        } catch (FeedbackNotFoundException e) {
            logger.info("No Feedbacks found by Event ID: " + eventId);
            throw e;
        }
    }

    /**
     * Deletes all feedbacks associated with a given attendee ID.
     * Throws an exception if no feedbacks are found.
     *
     * @param attendeeId the attendee ID for which all associated feedbacks are to be deleted.
     * @throws FeedbackNotFoundException if there are no feedbacks for the according attendee ID found.
     */
    public void deleteAllFeedbacksByAttendeeId(Long attendeeId) {
        try {
            Optional<List<Feedback>> optionalFeedbackList = Optional.ofNullable(findFeedbacksByAttendeeId(attendeeId));
            if (optionalFeedbackList.isPresent()) {
                List<Feedback> feedbackList = optionalFeedbackList.get();
                for (Feedback feedback : feedbackList) {
                    feedbackRepository.delete(feedback);
                    feedbackProducer.produceFeedbackEvent(feedback);
                }
            } else {
                throw new FeedbackNotFoundException("No Feedbacks found by Attendee ID: " + attendeeId);
            }
        } catch (FeedbackNotFoundException e) {
            logger.info("No Feedbacks found by Event ID: " + attendeeId);
            throw e;
        }
    }


    /**
     * Retrieves all the feedbacks from the repository.
     *
     * @return a list of all the feedbacks.
     */
    public List<Feedback> getAllFeedbacks() {
        return feedbackRepository.findAll();
    }

    /**
     * Retrieves a feedback associated with the provided feedback ID.
     * Throws an exception if no feedback is found with the provided ID.
     *
     * @param feedbackId the ID of the feedback to retrieve.
     * @return the feedback associated with the provided ID.
     * @throws FeedbackNotFoundException if no feedback is found for the provided ID.
     */
    public Feedback getFeedbackByFeedbackId(Long feedbackId) {
        Optional<Feedback> optionalFeedback = feedbackRepository.findByFeedbackId(feedbackId);
        if (optionalFeedback.isPresent()) {
            return optionalFeedback.get();
        } else {
            throw new FeedbackNotFoundException("Feedback not found with id: " + feedbackId);
        }
    }

    /**
     * Retrieves a list of feedbacks associated with the provided event ID.
     * Throws an exception if no feedback is found for the event ID or if the list of feedback is empty.
     *
     * @param eventId the ID of the event whose feedbacks to retrieve.
     * @return a list of feedbacks associated with the provided event ID.
     * @throws FeedbackNotFoundException if no feedbacks are found for the provided event ID or the list of feedbacks is empty.
     */
    public List<Feedback> findFeedbacksByEventId(Long eventId) {
        Optional<List<Feedback>> optionalFeedbackList = feedbackRepository.findFeedbackByEventId(eventId);
        if (optionalFeedbackList.isPresent()) {
            List<Feedback> feedbackList = optionalFeedbackList.get();
            if (feedbackList.size() == 0) {
                throw new FeedbackNotFoundException("No Feedback for the following EventID found: " + eventId);
            } else {
                return optionalFeedbackList.get();
            }
        } else {
            throw new FeedbackNotFoundException("No Feedback for the following EventID found: " + eventId);
        }
    }

    /**
     * Retrieves a list of feedbacks associated with the provided attendee ID.
     * Throws an exception if no feedback is found for the attendee ID or if the list of feedback is empty.
     *
     * @param attendeeId the ID of the attendee whose feedbacks to retrieve.
     * @return a list of feedbacks associated with the provided attendee ID.
     * @throws FeedbackNotFoundException if no feedbacks are found for the provided attendee ID or the list of feedbacks is empty.
     */
    public List<Feedback> findFeedbacksByAttendeeId(Long attendeeId) {
        Optional<List<Feedback>> optionalFeedbackList = feedbackRepository.findFeedbackByAttendeeId(attendeeId);
        if (optionalFeedbackList.isPresent()) {
            List<Feedback> feedbackList = optionalFeedbackList.get();
            if (feedbackList.size() == 0) {
                throw new FeedbackNotFoundException("No Feedback for the following AttendeeID found: " + attendeeId);
            } else {
                return optionalFeedbackList.get();
            }
        } else {
            throw new FeedbackNotFoundException("No Feedback for the following AttendeeID found: " + attendeeId);
        }
    }

    // TODO: Muss beim Controller implementiert werden
    /**
     * Retrieves the feedback associated with the provided attendee ID and event ID.
     * Throws a FeedbackNotFoundException if no feedback is found for the provided IDs.
     *
     * @param attendeeId the ID of the attendee.
     * @param eventId the ID of the event.
     * @return the feedback associated with the provided attendee ID and event ID.
     * @throws FeedbackNotFoundException if no feedback is found for the provided attendee ID and event ID.
     */
    public Feedback getFeedbackByAttendeeIdAndEventId(Long attendeeId, Long eventId) {
        Optional<Feedback> optionalFeedback = feedbackRepository.findByAttendeeIdAndEventId(attendeeId, eventId);
        if (optionalFeedback.isPresent()) {
            return optionalFeedback.get();
        } else {
            throw new FeedbackNotFoundException("No Feedback for the Attendee ID: "+attendeeId+" and Eventid "+eventId+" found.");
        }
    }

}