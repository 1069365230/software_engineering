package at.univie.davidreichert.feedbackservice.repo;

import at.univie.davidreichert.feedbackservice.model.Attendee;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AttendeeRepository extends JpaRepository<Attendee, Long> {
    @NotNull
    Optional<Attendee> findById(@NotNull Long id);

    Optional<Attendee> findByAttendeeId(Long attendeeId);


    Optional<Attendee> findAttendeeByAttendeeId(Long attendeeId);
}
