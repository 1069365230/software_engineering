package at.univie.davidreichert.analyticsandreport.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

@Entity
@Table(name = "ATTENDEE")
public class Attendee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendee_id")
    private Long id;

    @NotBlank
    @Column(name = "name")
    private String name;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "attendingevents", joinColumns = @JoinColumn(name = "attendee_id"))
    @Column(name = "event_id")
    private List<Long> attendingEventsIDs;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {return name;}

    public void setName(String name) {
        this.name = name;
    }

    public List<Long> getAttendingEventIDs() { return attendingEventsIDs; }

    public void setAttendingEventIDs(List<Long> attendingEventIds) {
        this.attendingEventsIDs = attendingEventIds;

    }
}
