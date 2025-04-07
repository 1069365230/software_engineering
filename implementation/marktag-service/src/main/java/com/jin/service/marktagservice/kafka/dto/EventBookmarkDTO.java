package com.jin.service.marktagservice.kafka.dto;

import com.jin.service.marktagservice.model.Tag;

import java.util.List;

public record EventBookmarkDTO(int attendeeId, int eventId, List<Tag> tags, boolean action) {
}
