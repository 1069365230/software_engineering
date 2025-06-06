package at.univie.davidreichert.feedbackservice.service;

import at.univie.davidreichert.feedbackservice.dto.incoming.EventDTO;
import at.univie.davidreichert.feedbackservice.model.Event;
import at.univie.davidreichert.feedbackservice.repo.EventRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EventService {
    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    /**
     * Processes an EventDTO object and performs the necessary actions based on its data.
     *
     * @param dto The EventDTO object to be processed.
     */
    public void processEvent(EventDTO dto) {

        Optional<Event> existingEvent = eventRepository.findEventById(dto.getId());

        Event event;
        if (existingEvent.isPresent()) {
            event = existingEvent.get();
        } else {
            event = new Event();
        }
        event.setId(dto.getId());
        event.setEventName(dto.getName());
        event.setOrganizerId(dto.getOrganizerId());
        event.setStartDate(dto.getStartDate());
        event.setEndDate(dto.getEndDate());
        eventRepository.save(event);
    }

}

