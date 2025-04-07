package com.ase.attendanceservice.jpa;

import com.ase.attendanceservice.model.Attendee;
import com.ase.attendanceservice.model.Event;
import com.ase.attendanceservice.model.Ticket;
import com.ase.attendanceservice.model.TicketType;
import com.ase.attendanceservice.repository.AttendeeRepository;
import com.ase.attendanceservice.repository.EventRepository;
import com.ase.attendanceservice.repository.TicketRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@DataJpaTest
@ActiveProfiles("test")
public class TicketRepositoryTest {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private AttendeeRepository attendeeRepository;

    private Event testEvent;
    private Attendee testAttendee;

    @BeforeEach
    public void setup() {
        // Setup test event and attendee to create tickets for
        testAttendee = new Attendee(1l, "firstname", "lastname", "email");
        attendeeRepository.save(testAttendee);

        testEvent = new Event(1l, 15l, "name", 200, Instant.now(), Instant.now().plus(Duration.ofDays(1)));
        eventRepository.save(testEvent);
    }

    @Test
    public void saveAndRetrieveTicketTest() {
        // GIVEN
        Ticket testTicket = new Ticket(testEvent, testAttendee, TicketType.GeneralAdmission, Instant.now(),
                Instant.now().plus(Duration.ofDays(1)), new byte[1]);

        // WHEN
        ticketRepository.saveAndFlush(testTicket);

        // THEN
        Assertions.assertEquals(testTicket, ticketRepository.findById(testTicket.getId()).get());
    }

    @Test
    public void ticketDeletionTest() {
        // GIVEN
        Ticket testTicket = new Ticket(testEvent, testAttendee, TicketType.GeneralAdmission, Instant.now(),
                Instant.now().plus(Duration.ofDays(1)), new byte[1]);

        // WHEN
        ticketRepository.saveAndFlush(testTicket);
        ticketRepository.delete(testTicket);

        // THEN
        Assertions.assertTrue(ticketRepository.findById(testTicket.getId()).isEmpty());
    }

    @Test
    public void duplicateTicketForAttendee_ShouldThrowDataIntegrityException() {
        Ticket testTicket = new Ticket(testEvent, testAttendee, TicketType.GeneralAdmission, Instant.now(),
                Instant.now().plus(Duration.ofDays(1)), new byte[1]);
        Ticket testTicket2 = new Ticket(testEvent, testAttendee, TicketType.GeneralAdmission, Instant.now(),
                Instant.now().plus(Duration.ofDays(1)), new byte[1]);

        // WHEN
        ticketRepository.saveAndFlush(testTicket);

        // THEN
        Assertions.assertThrows(DataIntegrityViolationException.class, () -> ticketRepository.saveAndFlush(testTicket2));
    }

    @Test
    public void storeEventWithoutCategory_ShouldSetCorrectDefaultValue() {
        Ticket testTicket = new Ticket();
        testTicket.setId(UUID.randomUUID());
        testTicket.setEvent(testEvent);
        testTicket.setAttendee(testAttendee);

        // WHEN
        ticketRepository.saveAndFlush(testTicket);

        // THEN
        Assertions.assertEquals(TicketType.GeneralAdmission, testTicket.getTicketType());
    }
}
