package at.univie.davidreichert.analyticsandreport.service;


import at.univie.davidreichert.analyticsandreport.dto.incoming.FeedbackDTO;
import at.univie.davidreichert.analyticsandreport.dto.converter.FeedbackDTOConverter;
import at.univie.davidreichert.analyticsandreport.model.Feedback;
import at.univie.davidreichert.analyticsandreport.repo.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;

    private final FeedbackDTOConverter feedbackDTOConverter;


    @Autowired
    public FeedbackService(FeedbackRepository feedbackRepository, FeedbackDTOConverter feedbackDTOConverter) {
        this.feedbackRepository = feedbackRepository;
        this.feedbackDTOConverter = feedbackDTOConverter;
    }

    /**
     * Processes the FeedbackDTO object and saves or deletes the
     * corresponding Feedback object based on its activity status.
     *
     * @param dto The FeedbackDTO object to be processed.
     */
    public void processFeedback(FeedbackDTO dto) {
        Feedback feedback;
        feedback = feedbackDTOConverter.feedbackDTOtoFeedback(dto);
        if (feedback.isActive()) {
            feedbackRepository.save(feedback);
        } else {
            feedbackRepository.delete(feedback);
        }
    }

    /**
     * Retrieves the list of feedbacks associated with the given event ID.
     *
     * @param eventId The ID of the event for which the feedbacks are being retrieved.
     * @return An Optional containing the list of feedbacks, or an empty Optional if no feedbacks are found.
     */
    public Optional<List<Feedback>> findFeedbackByEventId(Long eventId) {
        return feedbackRepository.findFeedbackByEventId(eventId);
    }

    /**
     * Retrieves the feedback associated with the given feedback ID.
     *
     * @param feedbackId The ID of the feedback to be retrieved.
     * @return An Optional containing the feedback, or an empty Optional if no feedback is found.
     */
    public Optional<Feedback> findFeedbackByFeedbackId(Long feedbackId) {
        return feedbackRepository.findFeedbackByFeedbackId(feedbackId);
    }

    /**
     * Retrieves the number of feedbacks for a given event.
     *
     * @param eventId The ID of the event for which the number of feedbacks is being retrieved.
     * @return The number of feedbacks for the event.
     */
    public Long getNumberOfFeedbacksForEvent(Long eventId) {
        return feedbackRepository.countDistinctFeedbacksByEventId(eventId);
    }


}
