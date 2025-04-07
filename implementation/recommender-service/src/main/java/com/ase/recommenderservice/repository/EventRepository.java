package com.ase.recommenderservice.repository;

import com.ase.recommenderservice.model.Event;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    /**
     * Decrements the vacancies of the specified event by one
     * if it is not already 0
     *
     * @param eventId The ID of the Event
     * @return The number of rows updated (0 if vacancy was already 0)
     */
    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Event SET vacancies = vacancies - 1 WHERE id = :eventId AND vacancies > 0")
    int updateEventVacancy(Long eventId);


    /**
     * Query to find all are possible recommendation candidates based on these conditions:
     * 1. The attendee is not yet attending the event
     * 2. The event has not yet happened
     * 3. The event still has vacancies
     *
     * @param attendeeId The ID of the Attendee
     * @return All Events matching the specified criteria
     */
    @Query(value = "SELECT * FROM event e WHERE CURRENT_TIMESTAMP < e.start_date AND e.vacancies > 0 AND " +
            "e.id NOT IN (SELECT event_id FROM event_booking WHERE attendee_id = :attendeeId);", nativeQuery = true)
    List<Event> retrieveEventCandidates(Long attendeeId);
}
