package com.jin.service.marktagservice.repo;

import com.jin.service.marktagservice.model.Attendee;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
class AttendeeRepoTest {
    @Autowired
    private AttendeeRepository dummyAttendeeRepository;

    @AfterEach
    public void cleanUP() {
        dummyAttendeeRepository.deleteAll();
    }

    @Test
    public void findAttendeeByGlobalIDTest() {
        //prerequisite
        Attendee attendeeDummy = new Attendee();
        attendeeDummy.setGlobalId(1);
        dummyAttendeeRepository.save(attendeeDummy);

        //function that is being tested
        Attendee actualAttendee = dummyAttendeeRepository.findByGlobalId(1).orElse(null);

        //verifying
        Attendee expectedAttendee = attendeeDummy;
        assertEquals(expectedAttendee, actualAttendee);
    }

    @Test
    public void insertAttendeeTest() {
        //prerequisite
        Attendee attendeeDummy = new Attendee();
        attendeeDummy.setGlobalId(1);

        //function that is being tested
        dummyAttendeeRepository.save(attendeeDummy);

        //verifying
        int id = dummyAttendeeRepository.findByGlobalId(1).orElse(null).getId();

        Attendee expectedAttendee = attendeeDummy;
        Attendee actualAttendee = dummyAttendeeRepository.findById(id).orElse(null);
        Attendee actualAttendeeFindByGlobalID = dummyAttendeeRepository.findByGlobalId(1).orElse(null);

        assertEquals(actualAttendee, actualAttendeeFindByGlobalID);
        assertEquals(expectedAttendee, actualAttendee);
    }

    @Test
    public void removeAttendeeTest() {
        //prerequisite
        Attendee attendeeDummy = new Attendee();
        attendeeDummy.setGlobalId(1);
        dummyAttendeeRepository.save(attendeeDummy);
        int id = dummyAttendeeRepository.findByGlobalId(1).orElse(null).getId();

        //function that is being tested
        dummyAttendeeRepository.deleteById(id);

        //verifying
        Attendee actualAttendee = dummyAttendeeRepository.findByGlobalId(1).orElse(null);
        assertNull(actualAttendee);
    }

    @Test
    public void updateAttendeeTest() {
        //prerequisite
        Attendee attendeeDummy = new Attendee();
        attendeeDummy.setGlobalId(1);
        dummyAttendeeRepository.save(attendeeDummy);
        Attendee updatedAttendee = dummyAttendeeRepository.findByGlobalId(1).orElse(null);
        updatedAttendee.setGlobalId(2);

        //function that is being tested
        dummyAttendeeRepository.save(updatedAttendee);

        //verifying
        Attendee actualAttendee = dummyAttendeeRepository.findByGlobalId(1).orElse(null);
        assertNull(actualAttendee);

        Attendee AttendeeWithGlobalID1 = dummyAttendeeRepository.findByGlobalId(1).orElse(null);
        assertNull(AttendeeWithGlobalID1);

        Attendee AttendeeWithGlobalID2 = dummyAttendeeRepository.findByGlobalId(2).orElse(null);
        assertNotNull(AttendeeWithGlobalID2);

        int expectedGlobalID = 2;
        int actualGlobalID = AttendeeWithGlobalID2.getGlobalId();
        assertEquals(expectedGlobalID, actualGlobalID);
    }

}