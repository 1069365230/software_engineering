package com.ase.recommenderservice.repository;

import com.ase.recommenderservice.model.Attendee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttendeeRepository extends JpaRepository<Attendee, Long> {
    // Retrieve all attendees which are located in the same city as the newly added event and subscribed to receive emails

    /**
     * Retrieve all Attendee Candidates which might be interested in a newly added event
     *
     * @param city The city of the held Event
     * @return a List of Attendees which are located in the same city as the newly added event and subscribed to receive emails
     */
    @Query(value = "SELECT * FROM attendee a " +
            "WHERE a.city = :city AND a.receive_promotional_emails = true ", nativeQuery = true)
    List<Attendee> findAttendeeCandidates(String city);
}
