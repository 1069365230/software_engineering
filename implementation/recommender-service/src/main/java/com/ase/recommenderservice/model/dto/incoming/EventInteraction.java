package com.ase.recommenderservice.model.dto.incoming;

public abstract class EventInteraction {
    private Long attendeeId;
    private Long eventId;
    private boolean active; // false if cancelled

    public EventInteraction() {
    }

    public EventInteraction(Long attendeeId, Long eventId, boolean active) {
        this.attendeeId = attendeeId;
        this.eventId = eventId;
        this.active = active;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
