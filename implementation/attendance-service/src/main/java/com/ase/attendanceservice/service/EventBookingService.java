package com.ase.attendanceservice.service;

import com.ase.attendanceservice.constants.MessageConstants;
import com.ase.attendanceservice.exceptions.AccessDeniedException;
import com.ase.attendanceservice.exceptions.OverbookingException;
import com.ase.attendanceservice.exceptions.ResourceAlreadyExistsException;
import com.ase.attendanceservice.exceptions.ResourceNotFoundException;
import com.ase.attendanceservice.model.Attendee;
import com.ase.attendanceservice.model.Event;
import com.ase.attendanceservice.model.EventBooking;
import com.ase.attendanceservice.model.Ticket;
import com.ase.attendanceservice.model.dto.incoming.BookingRequestDTO;
import com.ase.attendanceservice.model.dto.outgoing.EventBookingDTO;
import com.ase.attendanceservice.model.factory.TicketFactory;
import com.ase.attendanceservice.notifications.EventBookingNotification;
import com.ase.attendanceservice.repository.AttendeeRepository;
import com.ase.attendanceservice.repository.EventBookingRepository;
import com.ase.attendanceservice.repository.EventRepository;
import com.ase.attendanceservice.repository.TicketRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EventBookingService {
    private AttendeeRepository attendeeRepository;
    private EventRepository eventRepository;
    private EventBookingRepository eventBookingRepository;
    private TicketRepository ticketRepository;

    private TicketFactory ticketFactory;
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    public EventBookingService(AttendeeRepository attendeeRepository, EventRepository eventRepository,
                               EventBookingRepository eventBookingRepository, TicketRepository ticketRepository,
                               ApplicationEventPublisher eventPublisher, TicketFactory ticketFactory) {
        this.attendeeRepository = attendeeRepository;
        this.eventRepository = eventRepository;
        this.eventBookingRepository = eventBookingRepository;
        this.ticketRepository = ticketRepository;
        this.eventPublisher = eventPublisher;
        this.ticketFactory = ticketFactory;
    }

    /**
     * Process an EventBooking request and create an EventBooking if successful
     *
     * @param attendeeId        The Attendee making the request
     * @param bookingRequestDTO The booking information
     * @return The created EventBooking
     */
    public EventBooking processEventBooking(Long attendeeId, BookingRequestDTO bookingRequestDTO) {
        Long eventId = bookingRequestDTO.getEventId();

        Event attendingEvent = eventRepository.findById(eventId).orElseThrow(
                () -> new ResourceNotFoundException(MessageFormat.format(MessageConstants.ENTITY_NOT_FOUND, "Event", eventId)));

        Attendee attendee = attendeeRepository.findById(attendeeId).orElseThrow(
                () -> new ResourceNotFoundException(MessageFormat.format(MessageConstants.ENTITY_NOT_FOUND, "Attendee", attendeeId)));

        // Verify that attendee is not already attending the event
        checkDuplicateAttendance(attendeeId, eventId);

        // Verify vacancies of event
        updateEventVacancy(eventId);

        Ticket ticket = ticketFactory.generateTicket(attendee, attendingEvent);
        ticketRepository.save(ticket);

        // Create EventBooking
        EventBooking eventBooking = new EventBooking(attendee, attendingEvent, ticket, bookingRequestDTO.getTimestamp());
        eventBookingRepository.save(eventBooking);

        // publish EventBooking to observers
        eventPublisher.publishEvent(new EventBookingNotification(this, eventBooking, true));
        return eventBooking;
    }

    /**
     * Process and verify if the event booking is still within the 24h cancellation period
     *
     * @param attendeeId            The Attendee requesting the cancellation
     * @param eventId               The event for which the booking should be cancelled
     * @param cancellationTimestamp The timestamp when the cancellation was requested
     */
    public boolean processEventBookingCancellation(Long attendeeId, Long eventId, Instant cancellationTimestamp) {
        EventBooking eventBooking = eventBookingRepository.findByAttendeeIdAndEventId(attendeeId, eventId).orElseThrow(
                () -> new ResourceNotFoundException(MessageFormat.format(MessageConstants.EVENT_BOOKING_NOT_FOUND, attendeeId, eventId)));

        // Verify cancellation conditions
        boolean cancellationIsValid = verifyCancellationTimeConstraint(eventBooking.getEvent().getStartDate(), cancellationTimestamp);

        if (cancellationIsValid) {
            eventPublisher.publishEvent(new EventBookingNotification(this, eventBooking, false));
            eventBookingRepository.delete(eventBooking);
            // Update vacancies
            eventRepository.incrementEventVacancy(eventId);
            return true;
        } else
            return false;
    }

    /**
     * Retrieve the TicketQR-Code from an Attendee
     *
     * @param attendeeId The ID of the requesting Attendee
     * @param ticketId   The ID {@link UUID} of the Ticket
     * @return byte array containing the encoded QR-Code
     */
    public byte[] retrieveTicketQRCode(Long attendeeId, UUID ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(MessageConstants.ENTITY_NOT_FOUND, "Ticket", ticketId)));

        if (ticket.getAttendee().getId() != attendeeId)
            throw new AccessDeniedException(MessageFormat.format(MessageConstants.INVALID_ACCESS_PERMISSION, attendeeId));

        return ticket.getTicketQrCode();
    }

    /**
     * Retrieve all EventBookings for the attending Events of an Attendee
     *
     * @param attendeeId The ID of the requesting Attendee
     * @return List of EventBookingDTOs containing the information of all attending Events
     */
    @Transactional
    public List<EventBookingDTO> retrieveEventBookingsByAttendee(Long attendeeId) {
        attendeeRepository.findById(attendeeId).orElseThrow(
                () -> new ResourceNotFoundException(MessageFormat.format(MessageConstants.ENTITY_NOT_FOUND, "Attendee", attendeeId)));
        List<EventBookingDTO> eventBookings = eventBookingRepository.findByAttendeeId(attendeeId);
        return eventBookings;
    }

    /**
     * Check if an EventBooking can be cancelled, i.e. the cancellation request was made at least 24h before event start
     *
     * @param eventStart            The timestamp when the event begins
     * @param cancellationTimestamp The timestamp when the cancellation request was initiated
     * @return true if the cancellation is within time and can proceed
     */
    private boolean verifyCancellationTimeConstraint(Instant eventStart, Instant cancellationTimestamp) {
        Duration duration = Duration.between(cancellationTimestamp, eventStart);
        if (duration.compareTo(Duration.ofDays(1)) >= 0)
            return true;

        return false;
    }

    /**
     * Decrements the vacancies of an event if there are still vacancies
     *
     * @param eventId The event to check
     * @throws OverbookingException If the event is fully booked (no vacancies available)
     */
    private void updateEventVacancy(Long eventId) {
        int updatedRows = eventRepository.decrementEventVacancy(eventId);
        if (updatedRows == 0)
            throw new OverbookingException(MessageConstants.EVENT_OVERBOOKED);
    }

    /**
     * Check if the attendee is already attending the specified event
     *
     * @param attendeeId The Attendee making the booking request
     * @param eventId    The Event to be booked
     * @throws ResourceAlreadyExistsException If the EventBooking already exists
     */
    private void checkDuplicateAttendance(Long attendeeId, Long eventId) {
        Optional<EventBooking> booking = eventBookingRepository.findByAttendeeIdAndEventId(attendeeId, eventId);
        if (booking.isPresent())
            throw new ResourceAlreadyExistsException(MessageFormat.format(MessageConstants.DUPLICATE_REGISTRATION, attendeeId, eventId));
    }
}
