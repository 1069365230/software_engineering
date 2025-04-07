package com.jin.service.marktagservice.kafka.dto;

public record EventTagDTO(int attendeeId, int eventId, String tag, boolean action) {
}
