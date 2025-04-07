package at.univie.davidreichert.analyticsandreport.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity

public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long event_id;

    private String eventName;

    private Instant beginDate;
    private Instant endDate;

    private Long numberOfAttendees;

    private Long numberOfBookmarks;

    private Long numberOfFeedbacks;

    public void setEventId(Long event_id) {this.event_id = event_id; }

    public Long getEvent_id() { return event_id; }

    public void setEventName(String eventName) {this.eventName = eventName; }

    public String getEventName() { return eventName; }

    public void setBeginDate(Instant beginDate) {this.beginDate = beginDate; }

    public Instant getBeginDate() {return beginDate;}

    public void setEndDate(Instant endDate) { this.endDate = endDate;}

    public Instant getEndDate() {return endDate;}

    public void setNumberOfAttendees(Long numberOfAttendees) {
        this.numberOfAttendees = numberOfAttendees;
    }

    public Long getNumberOfAttendees() {return numberOfAttendees;}

    public void setnumberOfBookmarks(Long numberOfBookmarks) {
        this.numberOfBookmarks = numberOfBookmarks;
    }
    public Long getNumberOfBookmarks() {
        return numberOfBookmarks;
    }


    public Long getNumberOfFeedbacks() {
        return numberOfFeedbacks;
    }

    public void setNumberOfFeedbacks(Long numberOfFeedbacks) {
        this.numberOfFeedbacks = numberOfFeedbacks;
    }
}
