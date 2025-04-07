package com.jin.service.exportservice.model.repo;

import com.jin.service.exportservice.model.AttendingEvent;
import com.jin.service.exportservice.repo.AttendingRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class AttendingRepoTest {
    @Autowired
    private AttendingRepository attendingRepositoryDummy;

    @AfterEach
    public void cleanUP() {
        //this doesn't reset the internal database ID
        attendingRepositoryDummy.deleteAll();
    }

    @Test
    public void findAllByGlobalAttendeeIdTest() {
        //prerequisite
        AttendingEvent attendingEventDummy1 = new AttendingEvent();
        attendingEventDummy1.setGlobalAttendeeID(1);
        attendingEventDummy1.setGlobalEventID(1);

        AttendingEvent attendingEventDummy2 = new AttendingEvent();
        attendingEventDummy2.setGlobalAttendeeID(1);
        attendingEventDummy2.setGlobalEventID(2);

        AttendingEvent attendingEventDummy3 = new AttendingEvent();
        attendingEventDummy3.setGlobalAttendeeID(2);
        attendingEventDummy3.setGlobalEventID(2);

        attendingRepositoryDummy.save(attendingEventDummy1);
        attendingRepositoryDummy.save(attendingEventDummy2);
        attendingRepositoryDummy.save(attendingEventDummy3);

        List<AttendingEvent> expectedAttendingEvents = new ArrayList<>();
        expectedAttendingEvents.add(attendingEventDummy1);
        expectedAttendingEvents.add(attendingEventDummy2);

        //function that is being tested
        List<AttendingEvent> actualAttendingEvents = attendingRepositoryDummy.findAllByGlobalAttendeeId(1);

        //verifying
        assertEquals(expectedAttendingEvents, actualAttendingEvents);
    }

    @Test
    void findAllByGlobalAttendeeIdAndEventIdTest() {
        //prerequisite
        AttendingEvent attendingEventDummy1 = new AttendingEvent();
        attendingEventDummy1.setGlobalAttendeeID(1);
        attendingEventDummy1.setGlobalEventID(1);
        attendingRepositoryDummy.save(attendingEventDummy1);

        //function that is being tested
        AttendingEvent actualAttendingEvent = attendingRepositoryDummy.findAllByGlobalAttendeeIdAndEventId(1,1).orElse(null);

        //verifying
        AttendingEvent expectedAttendingEvent = attendingEventDummy1;
        assertEquals(expectedAttendingEvent, actualAttendingEvent);
    }

    @Test
    void insertAttendingEventTest() {
        //prerequisite
        AttendingEvent attendingEventDummy1 = new AttendingEvent();
        attendingEventDummy1.setGlobalAttendeeID(1);
        attendingEventDummy1.setGlobalEventID(1);

        //function that is being tested
        attendingRepositoryDummy.save(attendingEventDummy1);

        //verifying
        int id = attendingRepositoryDummy.findAllByGlobalAttendeeIdAndEventId(1, 1).orElse(null).getId();

        AttendingEvent expectedAttendingEvent = attendingEventDummy1;
        AttendingEvent actualAttendingEvent = attendingRepositoryDummy.findById(id).orElse(null);
        AttendingEvent actualAttendingEventFindByBothID = attendingRepositoryDummy.findAllByGlobalAttendeeIdAndEventId(1, 1).orElse(null);

        assertEquals(actualAttendingEvent, actualAttendingEventFindByBothID);
        assertEquals(expectedAttendingEvent, actualAttendingEvent);
    }

    @Test
    void removeAttendingEventTest() {
        //prerequisite
        AttendingEvent attendingEventDummy1 = new AttendingEvent();
        attendingEventDummy1.setGlobalAttendeeID(1);
        attendingEventDummy1.setGlobalEventID(1);
        attendingRepositoryDummy.save(attendingEventDummy1);
        int id = attendingRepositoryDummy.findAllByGlobalAttendeeIdAndEventId(1,1).orElse(null).getId();

        //function that is being tested
        attendingRepositoryDummy.deleteById(id);

        //verifying
        AttendingEvent actualAttendingEvent = attendingRepositoryDummy.findAllByGlobalAttendeeIdAndEventId(1,1).orElse(null);
        assertNull(actualAttendingEvent);
    }

    @Test
    void updateAttendingEventTest() {
        //prerequisite
        AttendingEvent attendingEventDummy1 = new AttendingEvent();
        attendingEventDummy1.setGlobalAttendeeID(1);
        attendingEventDummy1.setGlobalEventID(1);
        attendingRepositoryDummy.save(attendingEventDummy1);

        //function that is being tested
        AttendingEvent updatedAttendingEvent = attendingRepositoryDummy.findAllByGlobalAttendeeIdAndEventId(1,1).orElse(null);
        updatedAttendingEvent.setGlobalEventID(2);
        attendingRepositoryDummy.save(updatedAttendingEvent);

        //verifying
        attendingEventDummy1.setGlobalEventID(2);
        AttendingEvent expectedAttendingEvent = attendingEventDummy1;
        AttendingEvent actualAttendingEvent = attendingRepositoryDummy.findAllByGlobalAttendeeIdAndEventId(1,2).orElse(null);
        assertEquals(expectedAttendingEvent, actualAttendingEvent);
    }
}
