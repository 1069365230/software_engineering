package com.ase.attendanceservice.controller;


import com.ase.attendanceservice.exceptions.RequestException;
import com.ase.attendanceservice.model.Attendee;
import com.ase.attendanceservice.model.dto.incoming.MessageRequestDTO;
import com.ase.attendanceservice.service.MessagingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
public class MessagingController {
    private Logger logger = LoggerFactory.getLogger(MessagingController.class);
    private MessagingService messagingService;

    @Autowired
    public MessagingController(MessagingService messagingService) {
        this.messagingService = messagingService;
    }

    /**
     * Sends a message to the attendees of a specific event
     *
     * @param eventId        the ID of the event
     * @param messageRequest the MessageRequestDTO containing the message details
     * @return ResponseEntity with 201 No Content if successful
     */
    @PostMapping(path = "/{id}/messages", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<Void> messageAttendees(@PathVariable("id") Long eventId, @Valid @RequestBody MessageRequestDTO messageRequest) {
        messagingService.processAttendeeMessage(eventId, messageRequest);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Retrieves the list of attendees for a specific event
     *
     * @param eventId the ID of the event
     * @return ResponseEntity containing a list of Attendees {@link Attendee} which are
     * attending the specified Event
     */
    @GetMapping(path = "/{id}/attendees", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<List<Attendee>> getAttendeesByEvent(@PathVariable(name = "id") Long eventId) {
        List<Attendee> attendees = messagingService.retrieveAttendeesByEvent(eventId);
        return new ResponseEntity<>(attendees, HttpStatus.OK);
    }


    /**
     * ExceptionHandler for this REST-Controller
     * logs all errors incurred by invalid requests and returns the error message and HTTP status
     */
    @ExceptionHandler(RequestException.class)
    public ResponseEntity<String> handleException(HttpServletRequest request, RequestException exception) {
        logger.info("Request: " + request.getRequestURL() + " raised " + exception);
        return new ResponseEntity<>(exception.getMessage(), exception.getHttpStatus());
    }
}
