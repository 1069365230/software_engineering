
package at.univie.davidreichert.feedbackservice.model;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "EVENT")
public class Event {

    public Event(Long id, Long organizerId, String name, Instant startDate, Instant endDate) {
        this.id = id;
        this.organizerId = organizerId;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "organizerId")
    private Long organizerId;

    @Column(name = "name")
    private String name;

    @Column(name = "startDate")
    private Instant startDate;


    @Column(name = "endDate")
    private Instant endDate;



    public Event() {

    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public String getEventName() {
        return name;
    }

    public void setEventName(String name) {
        this.name = name;
    }

    public Long getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(Long organizerId) {
        this.organizerId = organizerId;
    }
}

