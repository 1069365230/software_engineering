package at.univie.davidreichert.analyticsandreport.dto.incoming;

import java.time.Instant;

public class EventDTO {
    private Long id;

    private Instant startDate;

    private Instant endDate;

    private Long organizerId;

    private String name;


    public EventDTO() {

    }

    public EventDTO(Long id, Instant startDate, Instant endDate, Long organizerId, String name) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.organizerId = organizerId;
        this.name = name;
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

    public Long getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(Long organizerId) {
        this.organizerId = organizerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
