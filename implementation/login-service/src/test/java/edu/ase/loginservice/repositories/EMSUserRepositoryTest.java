package edu.ase.loginservice.repositories;

import edu.ase.loginservice.models.EMSUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class EMSUserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EMSUserRepository userRepository;

    @Test
    public void whenFindByName_thenReturnEMSUser() {
        EMSUser alex = new EMSUser("alex", "alex", "alex", "alex", "ale", "AL", 'x', "test", "attendee");
        entityManager.persist(alex);
        entityManager.flush();

        EMSUser found = userRepository.findEMSUserByUsername(alex.getUsername());

        assertThat(found.getUsername())
                .isEqualTo(alex.getUsername());
    }

    @Test
    public void whenInvalidName_thenReturnNull() {
        EMSUser fromDb = userRepository.findEMSUserByUsername("doesNotExist");

        assertThat(fromDb).isNull();
    }

    @Test
    public void whenSaveUser_checkUserIsSaved() {
        EMSUser user = new EMSUser("john", "john", "john", "john", "jo", "JO", 'n', "test", "attendee");

        userRepository.save(user);

        EMSUser fromDb = userRepository.findEMSUserByUsername(user.getUsername());
        assertThat(fromDb).isEqualTo(user);
    }

    @Test
    public void whenDeleteUser_checkUserIsDeleted() {
        EMSUser user = new EMSUser("mike", "mike", "mike", "mike", "mi", "MI", 'e', "test", "attendee");
        entityManager.persist(user);
        entityManager.flush();

        userRepository.delete(user);

        EMSUser fromDb = userRepository.findEMSUserByUsername(user.getUsername());
        assertThat(fromDb).isNull();
    }

    @Test
    public void whenFindById_thenReturnUser() {
        EMSUser user = new EMSUser("jane", "jane", "jane", "jane", "ja", "JA", 'e', "test", "attendee");
        entityManager.persist(user);
        entityManager.flush();

        Optional<EMSUser> maybeUser = userRepository.findById(user.getId());
        
        assertThat(maybeUser.isPresent()).isTrue();
        assertThat(maybeUser.get().getId()).isEqualTo(user.getId());
    }
}
