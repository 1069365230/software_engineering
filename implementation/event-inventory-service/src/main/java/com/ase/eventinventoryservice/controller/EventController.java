package com.ase.eventinventoryservice.controller;

import com.ase.eventinventoryservice.exceptions.RequestException;
import com.ase.eventinventoryservice.model.Event;
import com.ase.eventinventoryservice.service.EventService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class EventController {
    private static final Logger logger = LoggerFactory.getLogger(EventController.class);
    private EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping(path = "events", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<Void> createEvent(@RequestBody @Valid Event event) {
        eventService.processEvent(event);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping(path = "events", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<Iterable<Event>> getAllEvents() {
        Iterable<Event> allEvents = eventService.getAllEvents();
        return new ResponseEntity<>(allEvents, HttpStatus.CREATED); // TODO: Status Created?
    }

    @PutMapping(path = "events/{eventId}")
    public ResponseEntity<Void> increaseEventDayByOne(@PathVariable("eventId") Long eventId) {
        if (!eventService.increaseEventDayByOne(eventId)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    /**
     * ExceptionHandler for this REST-Controller
     * logs all errors incurred by invalid requests and returns the error message and HTTP status
     */
    @ExceptionHandler(RequestException.class)
    public ResponseEntity<String> handleRequestException(HttpServletRequest request, RequestException exception) {
        logger.info("Request: " + request.getRequestURL() + " raised " + exception);
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
