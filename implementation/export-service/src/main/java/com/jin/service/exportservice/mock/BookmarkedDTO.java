package com.jin.service.exportservice.mock;

import java.util.List;

public record BookmarkedDTO(Long attendeeId, Long eventId, List<String> tags, boolean action) {
}
