package com.ase.attendanceservice.repository;

import com.ase.attendanceservice.model.Attendee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttendeeRepository extends JpaRepository<Attendee, Long> {

    /**
     * Retrieve all Attendees of a specific Event
     *
     * @param eventId The ID of the Event
     * @return List of Attendees attending this Event
     */
    @Query(value = "SELECT attendee.id, firstname, lastname, email FROM attendee INNER JOIN event_booking ON attendee.id = event_booking.attendee_id WHERE event_id = :eventId", nativeQuery = true)
    List<Attendee> findByEventId(Long eventId);

    /**
     * Retrieve the EMails of all Attendees specified
     *
     * @param ids List of Ids for which Attendees to retrieve the EMail
     * @return A List of Strings containing all EMails from the specified Ids
     */
    @Query(value = "SELECT email FROM attendee WHERE id IN :ids", nativeQuery = true)
    List<String> findEmailsByIds(List<Long> ids);

    /**
     * Retrieve the EMails of all Attendees in the Database
     *
     * @return A List of Strings containing all EMails
     */
    @Query(value = "SELECT email FROM attendee", nativeQuery = true)
    List<String> getAttendeeEmails();
}
