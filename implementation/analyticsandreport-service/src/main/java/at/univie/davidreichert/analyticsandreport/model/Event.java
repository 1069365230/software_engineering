
package at.univie.davidreichert.analyticsandreport.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "EVENT")
public class Event {

    @Id
    @Column(name = "id")
    private Long id;


    @Column(name = "startDate")
    private Instant startDate;


    @Column(name = "endDate")
    private Instant endDate;


    @Column(name = "organizerId")
    private Long organizerId;


    @Column(name = "name")
    private String name;


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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(Long organizerId) {
        this.organizerId = organizerId;
    }
}
