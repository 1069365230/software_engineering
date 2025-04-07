package edu.ems.notificationservice;

import edu.ems.notificationservice.dtos.AttendanceEntryDto;
import edu.ems.notificationservice.dtos.BookmarkEntryDto;
import edu.ems.notificationservice.dtos.EMSUserCreatedDto;
import edu.ems.notificationservice.models.EMSUser;
import edu.ems.notificationservice.models.Event;
import edu.ems.notificationservice.repositories.EMSUserRepository;
import edu.ems.notificationservice.repositories.EventRepository;
import edu.ems.notificationservice.stream.StreamConsumer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestEntityManager
public class NotificationServiceApplicationTest {
    @Autowired
    private EMSUserRepository emsUserRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private StreamConsumer streamConsumer;

    @AfterEach
    public void cleanup() {
        emsUserRepository.deleteAll();
        eventRepository.deleteAll();
        entityManager.clear();
    }

    @Transactional
    @Test
    public void testCreateUserFromStream() {
        streamConsumer.consumeEmsUserCreated().accept(new EMSUserCreatedDto(3L, "user3@example.com", "attendee"));
        entityManager.flush();

        assertTrue(emsUserRepository.existsById(3L));
    }

    @Transactional
    @Test
    public void testCreateEventFromAttendanceEntryStream() {
        streamConsumer.consumeEmsUserCreated().accept(new EMSUserCreatedDto(4L, "user4@example.com", "attendee"));
        streamConsumer.consumeAttendanceEntry().accept(new AttendanceEntryDto(4L, 2L, true));
        entityManager.flush();

        assertTrue(eventRepository.existsById(2L));
    }

    @Transactional
    @Test
    public void testCreateEventFromBookmarkEntryStream() {
        streamConsumer.consumeEmsUserCreated().accept(new EMSUserCreatedDto(5L, "user5@example.com", "attendee"));
        streamConsumer.consumeBookmarkEntry().accept(new BookmarkEntryDto(5L, 3L, true));
        entityManager.flush();

        assertTrue(eventRepository.existsById(3L));
    }

    @Transactional
    @Test
    public void testFailedUserCreationFromStream() {
        streamConsumer.consumeEmsUserCreated().accept(new EMSUserCreatedDto(null, "user6@example.com", "attendee"));
        entityManager.flush();

        assertNull(emsUserRepository.findEMSUserByEmail("user6@example.com"));
    }

    @Transactional
    @Test
    public void testBookmarkAndAttendanceBySameUser() {
        streamConsumer.consumeEmsUserCreated().accept(new EMSUserCreatedDto(6L, "user6@example.com", "attendee"));

        streamConsumer.consumeBookmarkEntry().accept(new BookmarkEntryDto(6L, 2L, true));
        streamConsumer.consumeAttendanceEntry().accept(new AttendanceEntryDto(2L, 6L, true));

        entityManager.flush();

        EMSUser savedUser = emsUserRepository.findById(6L).get();
        Event savedEvent = eventRepository.findById(2L).get();

        assertTrue(savedUser.getEvents().contains(savedEvent));
        assertTrue(savedEvent.getEmsUsers().contains(savedUser));
        assertEquals(1, savedEvent.getEmsUsers().stream().filter(user -> user.getId().equals(6L)).count());
    }

    @Transactional
    @Test
    public void testWhenTwoAttendeesAttendSameEvent() {
        streamConsumer.consumeEmsUserCreated().accept(new EMSUserCreatedDto(1L, "user1@example.com", "attendee"));
        streamConsumer.consumeAttendanceEntry().accept(new AttendanceEntryDto(1L, 1L, true));
        streamConsumer.consumeEmsUserCreated().accept(new EMSUserCreatedDto(2L, "user2@example.com", "attendee"));
        streamConsumer.consumeBookmarkEntry().accept(new BookmarkEntryDto(2L, 1L, true));

        entityManager.flush();

        EMSUser savedUser1 = emsUserRepository.findById(1L).get();
        EMSUser savedUser2 = emsUserRepository.findById(2L).get();
        Event savedEvent1 = eventRepository.findById(1L).get();


        assertTrue(savedUser1.getEvents().contains(savedEvent1));
        assertTrue(savedUser2.getEvents().contains(savedEvent1));
        assertTrue(savedEvent1.getEmsUsers().contains(savedUser1));
        assertTrue(savedEvent1.getEmsUsers().contains(savedUser2));
    }
}
