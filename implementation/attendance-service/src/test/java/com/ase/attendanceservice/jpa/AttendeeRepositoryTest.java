package com.ase.attendanceservice.jpa;


import com.ase.attendanceservice.model.Attendee;
import com.ase.attendanceservice.repository.AttendeeRepository;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
public class AttendeeRepositoryTest {
    @Autowired
    private AttendeeRepository attendeeRepository;

    @Test
    public void saveAndRetrieveAttendeeTest() {
        // GIVEN
        Attendee testAttendee = new Attendee(1l, "firstname", "lastname", "email");

        // WHEN
        attendeeRepository.saveAndFlush(testAttendee);

        // THEN
        Assertions.assertNotNull(attendeeRepository.findById(1l));
        Assertions.assertEquals(testAttendee, attendeeRepository.findById(1l).get());
    }

    @Test
    public void attendeeDeletionTest() {
        // GIVEN
        Attendee testAttendee = new Attendee(1l, "firstname", "lastname", "email");

        // WHEN
        attendeeRepository.saveAndFlush(testAttendee);
        attendeeRepository.delete(testAttendee);

        // THEN
        Assertions.assertTrue(attendeeRepository.findById(1l).isEmpty());
    }

    @Test
    public void attendeeDuplicateEmail_ShouldThrowException() {
        // GIVEN
        Attendee testAttendee = new Attendee(1l, "firstname", "lastname", "email");
        Attendee testAttendee2 = new Attendee(2l, "firstname", "lastname", "email");

        // WHEN
        attendeeRepository.save(testAttendee);

        // THEN
        Assertions.assertThrows(DataIntegrityViolationException.class, () -> attendeeRepository.saveAndFlush(testAttendee2));
    }

    @Test
    public void attendeeNullValues_ShouldThrowException() {
        // GIVEN
        Attendee testAttendee = new Attendee(1l, null, "lastname", "email");
        Attendee testAttendee2 = new Attendee(2l, "firstname", null, "email");
        Attendee testAttendee3 = new Attendee(3l, "firstname", "lastname", null);

        // WHEN | THEN
        Assertions.assertThrows(ConstraintViolationException.class, () -> attendeeRepository.saveAndFlush(testAttendee));
        Assertions.assertThrows(ConstraintViolationException.class, () -> attendeeRepository.saveAndFlush(testAttendee2));
        Assertions.assertThrows(ConstraintViolationException.class, () -> attendeeRepository.saveAndFlush(testAttendee3));
    }

    @Test
    public void attendeeEmptyStringValues_ShouldThrowException() {
        // GIVEN
        Attendee testAttendee = new Attendee(1l, "", "lastname", "email");
        Attendee testAttendee2 = new Attendee(2l, "firstname", "", "email");
        Attendee testAttendee3 = new Attendee(3l, "firstname", "lastname", "");

        // WHEN | THEN
        Assertions.assertThrows(ConstraintViolationException.class, () -> attendeeRepository.saveAndFlush(testAttendee));
        Assertions.assertThrows(ConstraintViolationException.class, () -> attendeeRepository.saveAndFlush(testAttendee2));
        Assertions.assertThrows(ConstraintViolationException.class, () -> attendeeRepository.saveAndFlush(testAttendee3));
    }
}