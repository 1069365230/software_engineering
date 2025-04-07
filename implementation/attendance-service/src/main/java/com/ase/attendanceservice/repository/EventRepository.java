package com.ase.attendanceservice.repository;

import com.ase.attendanceservice.model.Event;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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
    int decrementEventVacancy(Long eventId);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Event SET vacancies = vacancies + 1 WHERE id = :eventId and vacancies < maxCapacity")
    int incrementEventVacancy(Long eventId);
}
