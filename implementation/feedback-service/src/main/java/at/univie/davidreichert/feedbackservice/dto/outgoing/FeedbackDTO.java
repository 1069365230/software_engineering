package at.univie.davidreichert.feedbackservice.dto.outgoing;

public class FeedbackDTO {

    private Long feedbackId;

    private Long feedbackEventId;

    private Long feedbackAttendeeId;

    private int locationRating;

    private int descriptionRating;

    private int overallRating;

    private boolean active;

    public FeedbackDTO(Long feedbackID, Long feedbackEventId, Long feedbackAttendeeId, int locationRating, int descriptionRating, int overallRating, boolean active) {
        this.feedbackId = feedbackID;
        this.feedbackEventId = feedbackEventId;
        this.feedbackAttendeeId = feedbackAttendeeId;
        this.locationRating = locationRating;
        this.descriptionRating = descriptionRating;
        this.overallRating = overallRating;
    }

    public FeedbackDTO() {

    }

    public Long getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(Long feedbackID) {
        this.feedbackId = feedbackID;
    }

    public int getLocationRating() {
        return locationRating;
    }

    public void setLocationRating(int locationRating) {
        this.locationRating = locationRating;
    }

    public int getDescriptionRating() {
        return descriptionRating;
    }

    public void setDescriptionRating(int descriptionRating) {
        this.descriptionRating = descriptionRating;
    }

    public int getOverallRating() {
        return overallRating;
    }

    public void setOverallRating(int overallRating) {
        this.overallRating = overallRating;
    }

    public Long getFeedbackAttendeeId() {
        return feedbackAttendeeId;
    }

    public void setFeedbackAttendeeId(Long feedbackAttendeeId) {
        this.feedbackAttendeeId = feedbackAttendeeId;
    }

    public Long getFeedbackEventId() {
        return feedbackEventId;
    }

    public void setFeedbackEventId(Long feedbackEventId) {
        this.feedbackEventId = feedbackEventId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
