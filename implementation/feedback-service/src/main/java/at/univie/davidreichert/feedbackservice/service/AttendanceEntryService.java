package at.univie.davidreichert.feedbackservice.service;

import at.univie.davidreichert.feedbackservice.dto.incoming.AttendanceEntryDTO;
import at.univie.davidreichert.feedbackservice.model.AttendanceEntry;
import at.univie.davidreichert.feedbackservice.repo.AttendanceEntryRepository;
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

        Optional<AttendanceEntry> existingEntry = attendanceEntryRepository.findByAttendeeIdAndEventId(dto.getAttendeeId(), dto.getEventId());
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
     * Checks if an attendee is attending a specific event.
     *
     * @param attendeeId The id of the attendee to check.
     * @param eventId The id of the event to check.
     * @return true if the attendee is attending the event, false otherwise.
     */
    public boolean isAttending(Long attendeeId, Long eventId) {
        Optional<AttendanceEntry> activeAttendanceEntry = attendanceEntryRepository
                .findByAttendeeIdAndEventIdAndActive(attendeeId, eventId, true);

        return activeAttendanceEntry.isPresent();
    }
}
