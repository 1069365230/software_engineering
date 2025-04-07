package com.ase.attendanceservice.model.dto.incoming;

import java.time.Instant;

public class EventUpdateDTO {

    private Long eventId;

    private Instant newStartDate;

    private Instant newEndDate;

    public EventUpdateDTO() {
    }

    public EventUpdateDTO(Long eventId, Instant newStartDate, Instant newEndDate) {
        this.eventId = eventId;
        this.newStartDate = newStartDate;
        this.newEndDate = newEndDate;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Instant getNewStartDate() {
        return newStartDate;
    }

    public void setNewStartDate(Instant newStartDate) {
        this.newStartDate = newStartDate;
    }

    public Instant getNewEndDate() {
        return newEndDate;
    }

    public void setNewEndDate(Instant newEndDate) {
        this.newEndDate = newEndDate;
    }

    @Override
    public String toString() {
        return "EventUpdateDTO{" +
                "eventId=" + eventId +
                ", newStartDate=" + newStartDate +
                ", newEndDate=" + newEndDate +
                '}';
    }
}
