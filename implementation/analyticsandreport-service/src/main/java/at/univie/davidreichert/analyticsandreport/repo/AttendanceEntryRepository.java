package at.univie.davidreichert.analyticsandreport.repo;

import at.univie.davidreichert.analyticsandreport.model.AttendanceEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AttendanceEntryRepository extends JpaRepository<AttendanceEntry, Long> {
    boolean existsByAttendeeIdAndEventIdAndActive(Long attendeeId, Long eventId, boolean active);

    Optional<AttendanceEntry> findByAttendeeIdAndEventId(Long attendeeId, Long eventId);
    Optional<AttendanceEntry> findByAttendeeIdAndEventIdAndActive(Long attendeeId, Long eventId, boolean b);

    @Query("SELECT COUNT(DISTINCT e.attendeeId) FROM AttendanceEntry e WHERE e.eventId = ?1")
    Long countDistinctAttendeesByEventId(Long eventId);
}

