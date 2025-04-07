package com.ase.attendanceservice.model.dto.outgoing;

public class AttendanceEntryDTO {

    private Long attendeeId;

    private Long eventId;

    private boolean active; // false if booking was cancelled

    public AttendanceEntryDTO() {
    }

    public AttendanceEntryDTO(Long attendeeId, Long eventId, boolean active) {
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
