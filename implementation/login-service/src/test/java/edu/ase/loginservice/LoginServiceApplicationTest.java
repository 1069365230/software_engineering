package edu.ase.loginservice;

import edu.ase.loginservice.dtos.AuthorizationDto;
import edu.ase.loginservice.dtos.CreateEMSUserDto;
import edu.ase.loginservice.dtos.RoleAccessVerificationDto;
import edu.ase.loginservice.models.EMSUser;
import edu.ase.loginservice.repositories.EMSUserRepository;
import edu.ase.loginservice.services.AuthenticationService;
import edu.ase.loginservice.services.impls.UserManagementServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.util.AssertionErrors.*;

@SpringBootTest
@AutoConfigureTestEntityManager
public class LoginServiceApplicationTest {

    @Autowired
    private EMSUserRepository emsUserRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserManagementServiceImpl userManagementService;

    @Autowired
    private AuthenticationService authenticationService;

    @Transactional
    @Test
    public void testAuthenticateLogin() {
        userManagementService.createUser(new CreateEMSUserDto("x", "x", "x", "a", "a", "c", 'c', "test", "attendee"));
        entityManager.flush();
        String username = "x";
        String password = "x";

        ResponseEntity<AuthorizationDto> response = authenticationService.authenticateLogin(username, password);

        assertTrue(HttpStatus.OK == response.getStatusCode());
        assertTrue(response.getBody() != null);
        assertTrue(response.getBody().userId() > 0L);
        assertNotNull("Access Token Null", response.getBody().accessToken());
        assertTrue(!response.getBody().accessToken().isEmpty());
    }

    @Transactional
    @Test
    public void testVerifyRoleAccess() {
        userManagementService.createUser(new CreateEMSUserDto("f", "f", "f", "a", "a", "c", 'c', "test", "attendee"));
        entityManager.flush();
        ResponseEntity<AuthorizationDto> accessToken = authenticationService.authenticateLogin("f", "f");

        ResponseEntity<RoleAccessVerificationDto> response = authenticationService.verifyRoleAccess(accessToken.getBody().accessToken(), "analyticsandreport-service:/analytics/");

        assertTrue(HttpStatus.OK == response.getStatusCode());
        assertNotNull("Body is null", response.getBody().toString());
        assertEquals("role", "attendee", response.getBody().role());
        assertTrue(response.getBody().isVerified());
    }

    @Test
    @Transactional
    public void testCreateUser() {
        userManagementService.createUser(new CreateEMSUserDto("a", "a", "a", "a", "a", "c", 'c', "test", "attendee"));

        entityManager.flush();
        EMSUser emsUser = emsUserRepository.findEMSUserByUsername("a");
        assertNotNull("Assertion failed,  ems user is null", emsUser);
        assertEquals("Assertion failed, email is not as expected", emsUser.getEmail(), "a");
        assertEquals("Assertion failed, username is not as expected", emsUser.getUsername(), "a" +
                "");
    }

    @Test
    @Transactional
    public void testCreateUserWithInvalidInput() {
        ResponseEntity<String> response = userManagementService.createUser(new CreateEMSUserDto("b", "b", "a", "a", "a", "c", 'c', null, null));
        assertTrue(HttpStatus.BAD_REQUEST == response.getStatusCode());

        entityManager.flush();
        EMSUser emsUser = emsUserRepository.findEMSUserByUsername("b");
        assertNull("Assertion failed,  ems user is not null", emsUser);
    }

    @Test
    @Transactional
    public void testDeleteUser() {
        userManagementService.createUser(new CreateEMSUserDto("b", "b", "b", "b", "b", "d", 'd', "test", "attendee"));

        entityManager.flush();

        ResponseEntity<String> response = userManagementService.deleteUser("b");
        assertTrue(HttpStatus.OK == response.getStatusCode());

        EMSUser emsUser = emsUserRepository.findEMSUserByUsername("b");
        assertNull("Assertion failed,  ems user is not null", emsUser);
    }
}
