
package at.univie.davidreichert.feedbackservice.repo;
import at.univie.davidreichert.feedbackservice.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    Optional<Event> findEventById(Long eventId);
}

