package at.univie.davidreichert.feedbackservice.repo;


import at.univie.davidreichert.feedbackservice.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    Optional<Feedback> findByAttendeeIdAndEventId(Long attendeeId, Long eventId);

    Optional<Feedback> findByFeedbackId(Long feedbackId);

    Optional <List<Feedback>> findFeedbackByEventId(Long eventId);

    Optional<List<Feedback>> findFeedbackByAttendeeId(Long attendeeId);

    Optional<List<Feedback>> findAllByEventId(Long eventId);
}