package at.univie.davidreichert.feedbackservice.producer;

import at.univie.davidreichert.feedbackservice.dto.outgoing.FeedbackDTO;
import at.univie.davidreichert.feedbackservice.model.Feedback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;


@Component
public class FeedbackProducer {

    private final StreamBridge streamBridge;

    @Autowired
    public FeedbackProducer(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }


    public void produceFeedbackEvent(Feedback feedback) {
        FeedbackDTO newFeedbackDTO;
        newFeedbackDTO = feedbackTofeedbackDTO(feedback);
        streamBridge.send("attendee.feedback", newFeedbackDTO);
    }

    public void produceFeedbackRemovalEvent(Feedback feedback) {
        FeedbackDTO newFeedbackDTO;
        newFeedbackDTO = feedbackTofeedbackDTO(feedback);
        streamBridge.send("feedback.removal", newFeedbackDTO);
    }

    // Converts Feedback Entity to FeedbackDTO
    // TODO: Own Class --> Separation of Concerns
    private FeedbackDTO feedbackTofeedbackDTO(Feedback feedback) {
        FeedbackDTO newFeedbackDTO = new FeedbackDTO();
        newFeedbackDTO.setFeedbackId(feedback.getFeedbackId());
        newFeedbackDTO.setFeedbackAttendeeId(feedback.getAttendeeId());
        newFeedbackDTO.setFeedbackEventId(feedback.getEventId());
        newFeedbackDTO.setDescriptionRating(feedback.getDescriptionrating());
        newFeedbackDTO.setLocationRating(feedback.getLocationrating());
        newFeedbackDTO.setOverallRating(feedback.getOverallrating());
        newFeedbackDTO.setActive(feedback.isActive());
        return newFeedbackDTO;
    }


}
