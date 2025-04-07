package com.ase.attendanceservice.jpa;

import com.ase.attendanceservice.model.*;
import com.ase.attendanceservice.model.dto.outgoing.EventBookingDTO;
import com.ase.attendanceservice.repository.AttendeeRepository;
import com.ase.attendanceservice.repository.EventBookingRepository;
import com.ase.attendanceservice.repository.EventRepository;
import com.ase.attendanceservice.repository.TicketRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@DataJpaTest
@ActiveProfiles("test")
public class EventBookingRepositoryTest {
    @Autowired
    private AttendeeRepository attendeeRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventBookingRepository eventBookingRepository;

    @Autowired
    private TicketRepository ticketRepository;

    private Event testEvent;
    private Attendee testAttendee;


    @BeforeEach
    public void setup() {
        // Setup test event and attendee to create event-bookings for
        testAttendee = new Attendee(1l, "firstname", "lastname", "email");
        attendeeRepository.save(testAttendee);

        testEvent = new Event(1l, 15l, "name", 200, Instant.now(), Instant.now().plus(Duration.ofDays(1)));
        eventRepository.save(testEvent);
    }

    @Test
    public void storingEventBooking_ShouldCascadeStoreTicket() {
        // GIVEN
        Ticket testTicket = new Ticket(testEvent, testAttendee, TicketType.GeneralAdmission, Instant.now(),
                Instant.now().plus(Duration.ofDays(1)), new byte[1]);

        EventBooking testEventBooking = new EventBooking(testAttendee, testEvent, testTicket, Instant.now());

        // WHEN
        eventBookingRepository.saveAndFlush(testEventBooking);

        // THEN
        Assertions.assertEquals(testTicket, ticketRepository.findById(testTicket.getId()).get());
    }

    @Test
    public void deletingEventBooking_ShouldCascadeDeleteTicket() {
        // GIVEN
        Ticket testTicket = new Ticket(testEvent, testAttendee, TicketType.GeneralAdmission, Instant.now(),
                Instant.now().plus(Duration.ofDays(1)), new byte[1]);

        EventBooking testEventBooking = new EventBooking(testAttendee, testEvent, testTicket, Instant.now());

        // WHEN
        eventBookingRepository.saveAndFlush(testEventBooking);
        eventBookingRepository.delete(testEventBooking);

        // THEN
        Assertions.assertTrue(ticketRepository.findById(testTicket.getId()).isEmpty());
    }

    @Test
    public void findAttendeesByEvent() {
        // GIVEN
        Attendee testAttendee2 = new Attendee(2l, "firstname", "lastname", "email2");
        Attendee notAttending = new Attendee(3l, "firstname", "lastname", "email3");
        attendeeRepository.saveAll(Arrays.asList(testAttendee, testAttendee2, notAttending));

        EventBooking eventBooking = new EventBooking(testAttendee, testEvent, createTestTicket(testEvent, testAttendee), Instant.now());
        EventBooking eventBooking2 = new EventBooking(testAttendee2, testEvent, createTestTicket(testEvent, testAttendee2), Instant.now());
        eventBookingRepository.saveAllAndFlush(Arrays.asList(eventBooking, eventBooking2));

        // WHEN
        List<Attendee> eventAttendees = attendeeRepository.findByEventId(1l);

        // THEN
        Assertions.assertTrue(eventAttendees.containsAll(Arrays.asList(testAttendee, testAttendee2)));
        Assertions.assertFalse(eventAttendees.contains(notAttending));
    }

    @Test
    public void findEventBookingsByAttendee_ShouldReturnAllAttendeeBookings() {
        // GIVEN
        Event testEvent2 = new Event(2l, 5l, "name", 200, Instant.now(), Instant.now().plus(Duration.ofDays(1)));
        EventBooking eventBooking = new EventBooking(testAttendee, testEvent, createTestTicket(testEvent, testAttendee), Instant.now());
        EventBooking eventBooking2 = new EventBooking(testAttendee, testEvent2, createTestTicket(testEvent2, testAttendee), Instant.now());

        Attendee otherAttendee = new Attendee(2l, "firstname", "lastname", "email2");
        attendeeRepository.save(otherAttendee);
        EventBooking otherAttendeeBooking = new EventBooking(otherAttendee, testEvent, createTestTicket(testEvent, otherAttendee), Instant.now());

        eventRepository.save(testEvent2);
        eventBookingRepository.saveAllAndFlush(Arrays.asList(eventBooking, eventBooking2, otherAttendeeBooking));

        // WHEN
        List<EventBookingDTO> attendeeBookings = eventBookingRepository.findByAttendeeId(1l);

        // THEN
        Assertions.assertEquals(2, attendeeBookings.size());
        Assertions.assertTrue(attendeeBookings.stream().allMatch(booking -> booking.getAttendeeId() == 1));
    }

    private Ticket createTestTicket(Event event, Attendee attendee) {
        return new Ticket(event, attendee, TicketType.GeneralAdmission, Instant.now(), Instant.now(), new byte[0]);
    }
}
