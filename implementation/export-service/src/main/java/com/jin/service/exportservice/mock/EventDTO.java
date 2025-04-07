package com.jin.service.exportservice.mock;

import java.time.Instant;
import java.util.List;

public record EventDTO(Long eventId, Long organizerId, String name, String location, int capacity, String startDate, String endDate, boolean active) {
}
