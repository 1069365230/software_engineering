package at.univie.davidreichert.analyticsandreport.repo;

import at.univie.davidreichert.analyticsandreport.model.EventBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventBookmarkRepository extends JpaRepository<EventBookmark, Long> {

    Optional<EventBookmark> findEventBookmarkByAttendeeIdAndEventId(Long attendeeId, Long eventId);

    void deleteEventBookmarkByAttendeeIdAndEventId(Long attendeeId, Long eventId);

    @Query("SELECT COUNT(DISTINCT e.attendeeId) FROM EventBookmark e WHERE e.eventId = ?1")
    Long countDistinctAttendeesByEventId(Long eventId);

}
