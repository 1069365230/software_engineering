package at.univie.davidreichert.analyticsandreport.service;

import at.univie.davidreichert.analyticsandreport.dto.incoming.AttendanceEntryDTO;
import at.univie.davidreichert.analyticsandreport.model.AttendanceEntry;
import at.univie.davidreichert.analyticsandreport.repo.AttendanceEntryRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AttendanceEntryService {


    private final AttendanceEntryRepository attendanceEntryRepository;

    public AttendanceEntryService(AttendanceEntryRepository attendanceEntryRepository) {
        this.attendanceEntryRepository = attendanceEntryRepository;
    }

    /**
     * Processes a AttendanceEntryDTO and performs CRUD operations based on the DTO's data.
     *
     * @param dto The AttendanceEntryDTO object to be processed.
     */
    public void processAttendanceEntry(AttendanceEntryDTO dto) {

        Optional<AttendanceEntry> existingEntry = attendanceEntryRepository
                .findByAttendeeIdAndEventId(dto.getAttendeeId(), dto.getEventId());
        if (existingEntry.isPresent() && !dto.isActive()) {
            AttendanceEntry attendanceEntry = existingEntry.get();
            attendanceEntryRepository.delete(attendanceEntry);
        } else if (existingEntry.isPresent()) {
            AttendanceEntry attendanceEntry = existingEntry.get();
            attendanceEntry.setActive(dto.isActive());
            attendanceEntry.setEventId(dto.getEventId());
            attendanceEntry.setAttendeeId(dto.getAttendeeId());
            attendanceEntryRepository.save(attendanceEntry);
        } else {
            AttendanceEntry attendanceEntry = new AttendanceEntry();
            attendanceEntry.setAttendeeId(dto.getAttendeeId());
            attendanceEntry.setEventId(dto.getEventId());
            attendanceEntry.setActive(dto.isActive());
            attendanceEntryRepository.save(attendanceEntry);
        }
    }

    /**
     * Retrieves the number of attendees for a given event.
     *
     * @param eventId The ID of the event for which the number of attendees is being retrieved.
     * @return The number of attendees for the event.
     */
    public Long getNumberOfAttendeesForEvent(Long eventId) {
        // Returns zero if there is no attendee attending
        return attendanceEntryRepository.countDistinctAttendeesByEventId(eventId);
    }

}
