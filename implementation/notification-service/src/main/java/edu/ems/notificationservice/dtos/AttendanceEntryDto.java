package edu.ems.notificationservice.dtos;

public record AttendanceEntryDto(Long attendeeId, Long eventId, boolean active) {
}
