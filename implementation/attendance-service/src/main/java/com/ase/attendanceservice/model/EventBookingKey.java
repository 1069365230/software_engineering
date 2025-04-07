package com.ase.attendanceservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class EventBookingKey implements Serializable {
    @Column(name = "attendee_id")
    private Long attendeeId;

    @Column(name = "event_id")
    private Long eventId;

    public EventBookingKey() {
    }

    public Long getAttendeeId() {
        return attendeeId;
    }

    public void setAttendeeId(Long attendeeId) {
        this.attendeeId = attendeeId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventBookingKey that = (EventBookingKey) o;
        return Objects.equals(attendeeId, that.attendeeId) && Objects.equals(eventId, that.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attendeeId, eventId);
    }
}
