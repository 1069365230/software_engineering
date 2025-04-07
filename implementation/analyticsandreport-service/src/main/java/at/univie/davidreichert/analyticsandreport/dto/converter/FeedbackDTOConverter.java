package at.univie.davidreichert.analyticsandreport.dto.converter;

import at.univie.davidreichert.analyticsandreport.dto.incoming.FeedbackDTO;
import at.univie.davidreichert.analyticsandreport.model.Feedback;
import at.univie.davidreichert.analyticsandreport.repo.FeedbackRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class FeedbackDTOConverter {

    private final FeedbackRepository feedbackRepository;

    public FeedbackDTOConverter(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    /**
     * Converts a FeedbackDTO object to a Feedback object.
     *
     * @param dto The FeedbackDTO object to be converted.
     * @return A Feedback object populated with data from the FeedbackDTO object.
     */
    public Feedback feedbackDTOtoFeedback(FeedbackDTO dto) {
        Optional<Feedback> existingFeedback = feedbackRepository.findFeedbackByFeedbackId(dto.getFeedbackId());

        Feedback feedback;

        feedback = existingFeedback.orElseGet(Feedback::new);
        feedback.setFeedbackId(dto.getFeedbackId());
        feedback.setEventId(dto.getFeedbackEventId());
        feedback.setAttendeeId(dto.getFeedbackAttendeeId());
        feedback.setDescriptionRating(dto.getDescriptionRating());
        feedback.setLocationRating(dto.getLocationRating());
        feedback.setOverallrating(dto.getOverallRating());
        feedback.setActive(dto.isActive());
        return feedback;
    }
}
