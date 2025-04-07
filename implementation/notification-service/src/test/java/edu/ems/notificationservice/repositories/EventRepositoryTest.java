package edu.ems.notificationservice.repositories;

import edu.ems.notificationservice.models.EMSUser;
import edu.ems.notificationservice.models.Event;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class EventRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EventRepository eventRepository;

    @Test
    public void whenFindById_thenReturnEvent() {
        EMSUser alex = new EMSUser(1L, "alex@test.com");
        entityManager.persist(alex);
        Set<EMSUser> emsUsers = new HashSet<>();
        emsUsers.add(alex);

        Event event = new Event(1L, emsUsers);
        entityManager.persist(event);
        entityManager.flush();

        Optional<Event> found = eventRepository.findById(event.getId());

        assertThat(found.isPresent()).isTrue();
        assertThat(found.get().getId()).isEqualTo(event.getId());
    }

    @Test
    public void whenInvalidId_thenReturnNull() {
        Optional<Event> fromDb = eventRepository.findById(999L);

        assertThat(fromDb).isEmpty();
    }

    @Test
    public void whenSaveEvent_checkEventIsSaved() {
        EMSUser john = new EMSUser(2L, "john@test.com");
        entityManager.persist(john);
        Set<EMSUser> emsUsers = new HashSet<>();
        emsUsers.add(john);

        Event event = new Event(2L, emsUsers);
        eventRepository.save(event);

        Optional<Event> fromDb = eventRepository.findById(event.getId());
        assertThat(fromDb).isNotEmpty();
        assertThat(fromDb.get()).isEqualTo(event);
    }

    @Test
    public void whenDeleteEvent_checkEventIsDeleted() {
        EMSUser mike = new EMSUser(3L, "mike@test.com");
        entityManager.persist(mike);
        Set<EMSUser> emsUsers = new HashSet<>();
        emsUsers.add(mike);

        Event event = new Event(3L, emsUsers);
        entityManager.persist(event);
        entityManager.flush();

        eventRepository.delete(event);

        Optional<Event> fromDb = eventRepository.findById(event.getId());
        assertThat(fromDb).isEmpty();
    }
}

