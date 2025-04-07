package com.ase.attendanceservice.model.dto.incoming;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public class BookingRequestDTO {
    @NotNull
    private Long eventId;

    private final Instant timestamp = Instant.now();

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long attendingEventId) {
        this.eventId = attendingEventId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

}
