package com.jin.service.marktagservice.kafka.dto;

import java.time.Instant;
import java.util.List;

public record EventDTO(Long id, Long organizerId, String name, String location, int capacity, String startDate, String endDate) {
}
