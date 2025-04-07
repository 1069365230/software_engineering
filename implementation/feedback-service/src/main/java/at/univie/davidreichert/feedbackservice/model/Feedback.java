package at.univie.davidreichert.feedbackservice.model;


import jakarta.persistence.*;

@Entity
@Table(name = "feedback")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feedbackId;

    @Column(name = "eventId")
    private Long eventId;

    @Column(name = "attendeeId")
    private Long attendeeId;

    @Column(name = "attendeeName")
    private String attendeeName;

    @Column(name = "comment")
    private String comment;

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

    public Feedback(String attendeeName) {
        this.attendeeName = attendeeName;
    }

    public Feedback() {

    }


    public Long getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(Long feedbackId) {
        this.feedbackId = feedbackId;
    }

    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }

    public int getLocationrating() {return locationrating; }
    public void setLocationrating(int locationrating) { this.locationrating = locationrating; }

    public int getDescriptionrating() { return descriptionrating; }
    public void setDescriptionrating(int descriptionrating) { this.descriptionrating = descriptionrating; }

    public int getOverallrating() { return overallrating; }
    public void setOverallrating(int overallrating) { this.overallrating = overallrating; }


    public Long getAttendeeId() {
        return attendeeId;
    }

    public void setAttendeeId(Long attendeeId) {
        this.attendeeId = attendeeId;
    }

    public String getAttendeeName() {
        return attendeeName;
    }
    public void setAttendeeName(String attendeeName) {
        this.attendeeName = attendeeName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}