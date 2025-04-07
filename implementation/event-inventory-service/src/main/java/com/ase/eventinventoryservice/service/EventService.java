package com.ase.eventinventoryservice.service;

import com.ase.eventinventoryservice.dtos.EventChangedDto;
import com.ase.eventinventoryservice.exceptions.RequestException;
import com.ase.eventinventoryservice.model.Event;
import com.ase.eventinventoryservice.repository.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Service
public class EventService {
    private static final String EVENT_TOPIC = "event.inventory";
    private static final String EVENT_UPDATE_TOPIC = "event.inventory.update";
    private static final Logger LOGGER = LoggerFactory.getLogger(EventService.class);

    private EventRepository eventRepository;
    private StreamBridge streamBridge;

    @Autowired
    public EventService(EventRepository eventRepository, StreamBridge streamBridge) {
        this.eventRepository = eventRepository;
        this.streamBridge = streamBridge;
    }


    public void processEvent(Event event) {
        if (event.getEndDate().isBefore(event.getStartDate()))
            throw new RequestException("Start-Date must be before End-Date");

        eventRepository.save(event);
        LOGGER.info("Event {} saved successfully", event);

        // Publish the event
        streamBridge.send(EVENT_TOPIC, event);
    }

    public boolean increaseEventDayByOne(Long eventId) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);

        if (!optionalEvent.isPresent()) {
            return false;
        }

        Event event = optionalEvent.get();

        Instant prevStartDate = event.getStartDate();
        Instant prevEndDate = event.getEndDate();

        event.setStartDate(event.getStartDate().plus(Duration.ofDays(1)));
        event.setEndDate(event.getEndDate().plus(Duration.ofDays(1)));
        eventRepository.save(event);

        this.streamBridge.send(EVENT_UPDATE_TOPIC,
                new EventChangedDto(
                        eventId,
                        prevStartDate,
                        prevEndDate,
                        event.getStartDate(),
                        event.getEndDate()
                )
        );
        return true;
    }

    public Iterable<Event> getAllEvents() {
        return eventRepository.findAll();
    }
}
