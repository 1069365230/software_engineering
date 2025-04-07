package com.jin.service.exportservice.model.repo;

import com.jin.service.exportservice.model.BookmarkedEvent;
import com.jin.service.exportservice.repo.BookmarkedRepository;
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
public class BookmarkedRepoTest {

    @Autowired
    private BookmarkedRepository bookmarkedRepositoryDummy;

    @AfterEach
    public void cleanUP() {
        //this doesn't reset the internal database ID
        bookmarkedRepositoryDummy.deleteAll();
    }

    @Test
    void findAllByGlobalAttendeeIdTest() {
        //prerequisite
        BookmarkedEvent bookmarkedEventDummy1 = new BookmarkedEvent();
        bookmarkedEventDummy1.setGlobalAttendeeID(1);
        bookmarkedEventDummy1.setGlobalEventID(1);

        BookmarkedEvent bookmarkedEventDummy2 = new BookmarkedEvent();
        bookmarkedEventDummy2.setGlobalAttendeeID(1);
        bookmarkedEventDummy2.setGlobalEventID(2);

        BookmarkedEvent bookmarkedEventDummy3 = new BookmarkedEvent();
        bookmarkedEventDummy3.setGlobalAttendeeID(2);
        bookmarkedEventDummy3.setGlobalEventID(2);

        bookmarkedRepositoryDummy.save(bookmarkedEventDummy1);
        bookmarkedRepositoryDummy.save(bookmarkedEventDummy2);
        bookmarkedRepositoryDummy.save(bookmarkedEventDummy3);

        List<BookmarkedEvent> expectedBookmarkedEvents = new ArrayList<>();
        expectedBookmarkedEvents.add(bookmarkedEventDummy1);
        expectedBookmarkedEvents.add(bookmarkedEventDummy2);

        //function that is being tested
        List<BookmarkedEvent> actualBookmarkedEvents = bookmarkedRepositoryDummy.findAllByGlobalAttendeeId(1);

        //verifying
        assertEquals(expectedBookmarkedEvents, actualBookmarkedEvents);
    }

    @Test
    void findAllByGlobalAttendeeIdAndEventIdTest() {
        //prerequisite
        BookmarkedEvent bookmarkedEventDummy1 = new BookmarkedEvent();
        bookmarkedEventDummy1.setGlobalAttendeeID(1);
        bookmarkedEventDummy1.setGlobalEventID(1);
        bookmarkedRepositoryDummy.save(bookmarkedEventDummy1);

        //function that is being tested
        BookmarkedEvent actualBookmarkedEvent = bookmarkedRepositoryDummy.findAllByGlobalAttendeeIdAndEventId(1, 1).orElse(null);

        //verifying
        BookmarkedEvent expectedBookmarkedEvent = bookmarkedEventDummy1;
        assertEquals(expectedBookmarkedEvent, actualBookmarkedEvent);

    }

    @Test
    void insertBookmarkedEvent() {
        //prerequisite
        BookmarkedEvent bookmarkedEventDummy1 = new BookmarkedEvent();
        bookmarkedEventDummy1.setGlobalAttendeeID(1);
        bookmarkedEventDummy1.setGlobalEventID(1);

        //function that is being tested
        bookmarkedRepositoryDummy.save(bookmarkedEventDummy1);

        //verifying
        int id = bookmarkedRepositoryDummy.findAllByGlobalAttendeeIdAndEventId(1, 1).orElse(null).getId();

        BookmarkedEvent expectedBookmarkedEvent = bookmarkedEventDummy1;
        BookmarkedEvent actualBookmarkedEvent = bookmarkedRepositoryDummy.findById(id).orElse(null);
        BookmarkedEvent actualBookmarkedFindByBothID = bookmarkedRepositoryDummy.findAllByGlobalAttendeeIdAndEventId(1, 1).orElse(null);

        assertEquals(actualBookmarkedEvent, actualBookmarkedFindByBothID);
        assertEquals(expectedBookmarkedEvent, actualBookmarkedEvent);
    }

    @Test
    void removeBookmarkedEvent() {
        //prerequisite
        BookmarkedEvent bookmarkedEventDummy1 = new BookmarkedEvent();
        bookmarkedEventDummy1.setGlobalAttendeeID(1);
        bookmarkedEventDummy1.setGlobalEventID(1);
        bookmarkedRepositoryDummy.save(bookmarkedEventDummy1);
        int id = bookmarkedRepositoryDummy.findAllByGlobalAttendeeIdAndEventId(1,1).orElse(null).getId();

        //function that is being tested
        bookmarkedRepositoryDummy.deleteById(id);

        //verifying
        BookmarkedEvent actualBookmarkedEvent = bookmarkedRepositoryDummy.findAllByGlobalAttendeeIdAndEventId(1,1).orElse(null);
        assertNull(actualBookmarkedEvent);

    }

    @Test
    void updateBookmarkedEvent() {
        //prerequisite
        BookmarkedEvent bookmarkedEventDummy1 = new BookmarkedEvent();
        bookmarkedEventDummy1.setGlobalAttendeeID(1);
        bookmarkedEventDummy1.setGlobalEventID(1);
        bookmarkedRepositoryDummy.save(bookmarkedEventDummy1);
        BookmarkedEvent updatedBookmarkedEvent = bookmarkedRepositoryDummy.findAllByGlobalAttendeeIdAndEventId(1,1).orElse(null);
        updatedBookmarkedEvent.setGlobalEventID(2);

        //function that is being tested
        bookmarkedRepositoryDummy.save(updatedBookmarkedEvent);
        bookmarkedEventDummy1.setGlobalEventID(2);

        //verifying
        BookmarkedEvent expectedBookmarkedEvent = bookmarkedEventDummy1;
        BookmarkedEvent actualBookmarkedEvent = bookmarkedRepositoryDummy.findAllByGlobalAttendeeIdAndEventId(1,2).orElse(null);
        assertEquals(expectedBookmarkedEvent, actualBookmarkedEvent);
    }
}
