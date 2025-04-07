package com.ase.attendanceservice.model.dto.incoming;

import java.time.Instant;

public class EventDTO {
    private Long id;

    private Long organizerId;

    private String name;

    private int maxCapacity;

    private Instant startDate;

    private Instant endDate;

    public EventDTO() {
    }

    public EventDTO(Long id, Long organizerId, String name, int maxCapacity, Instant startDate, Instant endDate) {
        this.id = id;
        this.organizerId = organizerId;
        this.name = name;
        this.maxCapacity = maxCapacity;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
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

    @Override
    public String toString() {
        return "EventDTO{" +
                "eventId=" + id +
                ", organizerId=" + organizerId +
                ", name='" + name + '\'' +
                ", capacity=" + maxCapacity +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
