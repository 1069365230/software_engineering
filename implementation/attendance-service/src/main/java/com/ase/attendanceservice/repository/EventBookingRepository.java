package com.ase.attendanceservice.repository;

import com.ase.attendanceservice.model.EventBooking;
import com.ase.attendanceservice.model.dto.outgoing.EventBookingDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventBookingRepository extends JpaRepository<EventBooking, Long> {
    /**
     * Retrieve a specific event-booking of an Attendee
     *
     * @param attendeeId the ID of the Attendee
     * @param eventId    the ID of the Event
     * @return An Optional EventBooking
     */
    Optional<EventBooking> findByAttendeeIdAndEventId(Long attendeeId, Long eventId);

    /**
     * Retrieves all eventBookings of an Attendee
     *
     * @param attendeeId the ID of the Attendee
     * @return a list of EventBookingDTO objects containing the attendee's event bookings
     */
    @Query(value = "SELECT NEW com.ase.attendanceservice.model.dto.outgoing.EventBookingDTO(a.id, a.firstname, a.lastname, e.id, e.name, t.id, t.ticketType, t.validFrom, t.validTo) " +
            "FROM Ticket AS t " +
            "INNER JOIN Attendee AS a ON t.attendee.id = a.id " +
            "INNER JOIN Event AS e ON e.id = t.event.id " +
            "WHERE a.id = :attendeeId")
    List<EventBookingDTO> findByAttendeeId(Long attendeeId);
}
