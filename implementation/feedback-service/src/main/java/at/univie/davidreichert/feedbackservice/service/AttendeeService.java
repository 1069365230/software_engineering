package at.univie.davidreichert.feedbackservice.service;
import at.univie.davidreichert.feedbackservice.dto.incoming.AttendeeDTO;
import at.univie.davidreichert.feedbackservice.exceptions.AttendeeNotFoundException;
import at.univie.davidreichert.feedbackservice.model.Attendee;
import at.univie.davidreichert.feedbackservice.repo.AttendeeRepository;
import org.springframework.stereotype.Service;


import java.util.Optional;

@Service
public class AttendeeService {

    private final AttendeeRepository attendeeRepository;

    public AttendeeService(AttendeeRepository attendeeRepository) {
        this.attendeeRepository = attendeeRepository;
    }

    /**
     * Processes a received EMSUserRegistrationDTO and performs CRUD operations.
     *
     * @param dto The data transfer object containing attendee information.
     * @throws IllegalArgumentException if the dto is null or the provided id does not exist.
     */
    public void processAttendeeRegistration(AttendeeDTO dto) {

        Optional<Attendee> existingAttendee = attendeeRepository.findByAttendeeId(dto.getId());

        Attendee attendee;
        if (existingAttendee.isPresent()) {
            attendee = existingAttendee.get();
        } else {
            attendee = new Attendee();
        }
        attendee.setAttendeeId(dto.getId());
        attendee.setUsername(dto.getUsername());
        attendee.setEmail(dto.getEmail());
        attendee.setAttendeeForename (dto.getForename());
        attendee.setAttendeeSurname(dto.getSurname());
        attendee.setCompleteName(dto.getForename() + " " + dto.getSurname());
        attendee.setRole(dto.getRole());
        attendeeRepository.save(attendee);

    }

    public boolean attendeeExists(Long attendeeId) {
        Optional<Attendee> existingAttendee = attendeeRepository
                .findByAttendeeId(attendeeId);

        return existingAttendee.isPresent();

    }

    /**
     * Finds and returns the complete name of an attendee by their id.
     *
     * @param attendeeId The id of the attendee to find.
     * @return The complete name of the attendee.
     * @throws AttendeeNotFoundException if the attendeeId is null or does not exist in the repository.
     */
    public String findNameByAttendeeId(Long attendeeId) {

        Optional<Attendee> optionalAttendee = attendeeRepository.findAttendeeByAttendeeId(attendeeId);

        Attendee attendee;

        if (optionalAttendee.isPresent()) {
            attendee = optionalAttendee.get();
            return attendee.getCompleteName();
        } else {
            throw new AttendeeNotFoundException("AttendeeId: " + attendeeId + " not found.");
        }
    }
}


