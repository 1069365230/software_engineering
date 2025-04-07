package com.ase.attendanceservice.controller;

import com.ase.attendanceservice.constants.MessageConstants;
import com.ase.attendanceservice.exceptions.RequestException;
import com.ase.attendanceservice.model.EventBooking;
import com.ase.attendanceservice.model.dto.incoming.BookingRequestDTO;
import com.ase.attendanceservice.model.dto.outgoing.EventBookingDTO;
import com.ase.attendanceservice.service.EventBookingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/attendees")
public class EventBookingController {
    private Logger logger = LoggerFactory.getLogger(EventBookingController.class);
    private EventBookingService eventBookingService;

    @Autowired
    public EventBookingController(EventBookingService eventBookingService) {
        this.eventBookingService = eventBookingService;
    }

    /**
     * Books an event for a specific attendee
     *
     * @param attendeeID     the ID of the attendee
     * @param bookingRequest the BookingRequestDTO containing the booked eventId and a generated timestamp
     * @return ResponseEntity containing the EventBooking with a 201 Status Code if successful
     */
    @PostMapping(path = "/{id}/event-bookings", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<EventBooking> bookEvent(@PathVariable(name = "id") Long attendeeID, @RequestBody @Valid BookingRequestDTO bookingRequest) {
        EventBooking eventBooking = eventBookingService.processEventBooking(attendeeID, bookingRequest);
        return new ResponseEntity<>(eventBooking, HttpStatus.CREATED);
    }

    /**
     * Request cancellation of an EventBooking [Bookings can be cancelled up until 1 day until the Event starts]
     *
     * @param request    the HTTP servlet request
     * @param attendeeId the ID of the attendee
     * @param eventId    the ID of the event to cancel the booking for
     * @return ResponseEntity with a success status code if the cancellation is approved
     * or an error message with a conflict status code if the cancellation window has expired
     */
    @DeleteMapping(path = "/{id}/event-bookings", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<String> cancelEventBooking(HttpServletRequest request, @PathVariable(name = "id") Long attendeeId,
                                                                   @RequestParam Long eventId) {
        Instant cancellationTimestamp = retrieveCancellationRequestTimestamp(request);
        boolean cancellationApproved = eventBookingService.processEventBookingCancellation(attendeeId, eventId, cancellationTimestamp);

        if (cancellationApproved)
            return ResponseEntity.noContent().build();
        else
            return new ResponseEntity<>(MessageConstants.CANCELLATION_WINDOW_EXPIRED, HttpStatus.CONFLICT);
    }

    /**
     * Retrieves all event bookings for a specific attendee
     *
     * @param attendeeId the ID of the attendee
     * @return ResponseEntity containing a list of EventBookingDTO objects representing the event bookings,
     * with each booking containing a HATEOAS link to its QR code
     */
    @GetMapping(path = "/{id}/event-bookings", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<List<EventBookingDTO>> getAllEventBookingsByAttendee(@PathVariable(name = "id") Long attendeeId) {
        List<EventBookingDTO> eventBookings = eventBookingService.retrieveEventBookingsByAttendee(attendeeId);

        // Add HATEOAS link for each ticket to its QR-Code
        for (EventBookingDTO dto : eventBookings)
            dto.add(linkTo(methodOn(EventBookingController.class).getAttendeeTicketQrCode(attendeeId, dto.getTicketSerialNr())).withRel("QR-Code"));

        return new ResponseEntity<>(eventBookings, HttpStatus.OK);
    }

    /**
     * Retrieves the QR code image for a specific attendee's ticket and returns
     * the QR-Code in JPG format
     *
     * @param attendeeId the ID of the attendee
     * @param ticketId   the UUID of the ticket
     * @return ResponseEntity containing the byte array of the QR code image
     */
    @GetMapping(path = "/{id}/tickets/{ticketId}", produces = MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody ResponseEntity<byte[]> getAttendeeTicketQrCode(@PathVariable(name = "id") Long attendeeId, @PathVariable UUID ticketId) {
        byte[] qrCode = eventBookingService.retrieveTicketQRCode(attendeeId, ticketId);
        return new ResponseEntity<>(qrCode, HttpStatus.OK);
    }


    /**
     * Get the cancellationTimestamp from the made request
     *
     * @param request The HTTPServletRequest
     * @return the cancellation timestamp from the HTTP-Header if it is available, else return Instant.now()
     */
    private Instant retrieveCancellationRequestTimestamp(HttpServletRequest request) {
        Instant cancellationTimestamp = Instant.now();

        // Retrieve the request timestamp from the 'Date' Header if it is available
        String requestTimestamp = request.getHeader("Date");
        if (request.getHeader("Date") != null)
            cancellationTimestamp = DateTimeFormatter.RFC_1123_DATE_TIME.parse(requestTimestamp, Instant::from);

        return cancellationTimestamp;
    }

    /**
     * ExceptionHandler for this REST-Controller
     * logs all errors incurred by invalid requests and returns the error message and HTTP status
     */
    @ExceptionHandler(RequestException.class)
    public ResponseEntity<String> handleRequestException(HttpServletRequest request, RequestException exception) {
        logger.info("Request: " + request.getRequestURL() + " raised " + exception);
        return new ResponseEntity<>(exception.getMessage(), exception.getHttpStatus());
    }
}
