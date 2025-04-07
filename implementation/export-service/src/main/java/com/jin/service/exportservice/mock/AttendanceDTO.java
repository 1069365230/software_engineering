package com.jin.service.exportservice.mock;

import java.util.List;

public record AttendanceDTO(Long attendeeId, Long eventId, List<String> tags, boolean action) {

}
