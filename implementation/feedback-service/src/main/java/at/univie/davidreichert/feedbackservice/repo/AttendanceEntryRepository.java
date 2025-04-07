package at.univie.davidreichert.feedbackservice.repo;

import at.univie.davidreichert.feedbackservice.model.AttendanceEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AttendanceEntryRepository extends JpaRepository<AttendanceEntry, Long> {
    boolean existsByAttendeeIdAndEventIdAndActive(Long attendeeId, Long eventId, boolean active);

    Optional<AttendanceEntry> findByAttendeeIdAndEventId(Long attendeeId, Long eventId);

    Optional<AttendanceEntry> findByAttendeeIdAndEventIdAndActive(Long attendeeId, Long eventId, boolean b);
}
