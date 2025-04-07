package com.ase.eventinventoryservice.dtos;

import java.time.Instant;

public record EventChangedDto(Long eventId, Instant prevStartDate, Instant prevEndDate, Instant newStartDate,
                              Instant newEndDate) {
}
