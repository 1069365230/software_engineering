
package at.univie.davidreichert.analyticsandreport.repo;
import at.univie.davidreichert.analyticsandreport.model.Event;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    @NotNull
    Optional<Event> findById(@NotNull Long id);

    Optional<Event> findEventById(Long id);

}

