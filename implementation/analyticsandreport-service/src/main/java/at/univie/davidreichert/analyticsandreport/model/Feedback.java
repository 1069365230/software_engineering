package at.univie.davidreichert.analyticsandreport.model;


import jakarta.persistence.*;

@Entity
@Table(name = "feedback")
public class Feedback {

    @Id
    private Long feedbackId;

    @Column(name = "eventId")
    private Long eventId;

    @Column(name = "attendeeId")
    private Long attendeeId;

    //@NotBlank(message = "Location Rating must not be blank.")
    @Column(name = "locationrating")
    private int locationrating;
    //@NotBlank(message = "Accuracy of Description Rating must not be blank.")
    @Column(name = "descriptionrating")
    private int descriptionrating;

    @Column(name = "overallrating")
    private int overallrating;

    @Column(name = "active")
    private boolean active;

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public int getLocationRating() {
        return locationrating;
    }

    public void setLocationRating(int locationrating) {
        this.locationrating = locationrating;
    }

    public int getDescriptionRating() {
        return descriptionrating;
    }

    public void setDescriptionRating(int descriptionrating) {
        this.descriptionrating = descriptionrating;
    }

    public int getOverallrating() {
        return overallrating;
    }

    public void setOverallrating(int overallrating) {
        this.overallrating = overallrating;
    }


    public Long getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(Long feedbackId) {
        this.feedbackId = feedbackId;
    }

    public Long getAttendeeId() {
        return attendeeId;
    }

    public void setAttendeeId(Long attendeeId) {
        this.attendeeId = attendeeId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}