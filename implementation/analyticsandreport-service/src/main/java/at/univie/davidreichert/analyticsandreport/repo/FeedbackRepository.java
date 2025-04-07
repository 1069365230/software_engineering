package at.univie.davidreichert.analyticsandreport.repo;


import at.univie.davidreichert.analyticsandreport.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    Optional<Feedback> findByAttendeeIdAndEventId(Long attendeeId, Long eventId);

    Optional<Feedback> findByFeedbackId(Long feedbackId);

    Optional <List<Feedback>> findFeedbackByEventId(Long eventId);

    Optional<List<Feedback>> findFeedbackByAttendeeId(Long attendeeId);

    Optional<Feedback> findFeedbackByFeedbackId(Long feedbackID);

    @Query("SELECT COUNT(DISTINCT e.feedbackId) FROM Feedback e WHERE e.eventId = ?1")
    Long countDistinctFeedbacksByEventId(Long eventId);
    

}