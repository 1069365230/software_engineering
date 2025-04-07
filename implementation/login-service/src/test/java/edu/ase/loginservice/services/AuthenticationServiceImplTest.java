package edu.ase.loginservice.services;

import edu.ase.loginservice.dtos.AuthorizationDto;
import edu.ase.loginservice.dtos.RoleAccessVerificationDto;
import edu.ase.loginservice.models.EMSUser;
import edu.ase.loginservice.repositories.EMSUserRepository;
import edu.ase.loginservice.services.helpers.JWTService;
import edu.ase.loginservice.services.helpers.KeycloakRequestService;
import edu.ase.loginservice.services.helpers.RoleMappingService;
import edu.ase.loginservice.testserviceimpls.AuthenticationServiceTestImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    @Mock
    private JWTService jwtService;

    @Mock
    private RoleMappingService roleMappingService;

    @Mock
    private EMSUserRepository emsUserRepository;

    @InjectMocks
    private AuthenticationServiceTestImpl authenticationServiceTest;

    @Test
    void verifyRoleAccess_ValidRole_ReturnsRoleAccessVerificationDto() {
        String accessToken = "validAccessToken";
        String endpoint = "/api/admin";
        String role = "administrator";

        List<String> userRoles = Arrays.asList(role);
        when(jwtService.verifyAccessToken(accessToken)).thenReturn(Optional.of(userRoles));
        when(roleMappingService.verifyRoleAccess(userRoles, endpoint)).thenReturn(role);

        // Act
        ResponseEntity<RoleAccessVerificationDto> response = authenticationServiceTest.verifyRoleAccess(accessToken, endpoint);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertTrue(Objects.requireNonNull(response.getBody()).isVerified());
        assertEquals(role, response.getBody().role());
        assertEquals(endpoint, response.getBody().endpoint());
    }

    @Test
    void verifyRoleAccess_InvalidRole_ReturnsRoleAccessVerificationDto() {
        String accessToken = "validAccessToken";
        String endpoint = "/api/admin";
        List<String> userRoles = Arrays.asList("attendee");
        when(jwtService.verifyAccessToken(accessToken)).thenReturn(Optional.of(userRoles));
        when(roleMappingService.verifyRoleAccess(userRoles, endpoint)).thenReturn(null);

        ResponseEntity<RoleAccessVerificationDto> response = authenticationServiceTest.verifyRoleAccess(accessToken, endpoint);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertFalse(Objects.requireNonNull(response.getBody()).isVerified());
        assertNull(response.getBody().role());
        assertEquals(endpoint, response.getBody().endpoint());
    }

    @Test
    void verifyRoleAccess_InvalidAccessToken_ReturnsUnauthorizedStatus() {
        String accessToken = "invalidAccessToken";
        String endpoint = "/api/admin";
        when(jwtService.verifyAccessToken(accessToken)).thenReturn(Optional.empty());

        ResponseEntity<RoleAccessVerificationDto> response = authenticationServiceTest.verifyRoleAccess(accessToken, endpoint);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void authenticateLogin_ValidCredentials_ReturnsAuthorizationDto() {
        String username = "john";
        String password = "password";
        String accessToken = "validAccessToken";
        EMSUser emsUser = new EMSUser();

        KeycloakRequestService mockedKeycloakRequestService = mock(KeycloakRequestService.class);
        when(mockedKeycloakRequestService.makeKeycloakRequest(any(), any(), any())).thenReturn(ResponseEntity.ok("response"));
        when(mockedKeycloakRequestService.fromKeycloakResponseJson(anyString(), anyString())).thenReturn(accessToken);
        authenticationServiceTest.setKeycloakRequestService(mockedKeycloakRequestService);

        when(emsUserRepository.findEMSUserByUsernameAndPassword(username, password)).thenReturn(emsUser);

        ResponseEntity<AuthorizationDto> response = authenticationServiceTest.authenticateLogin(username, password);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(emsUser.getId(), response.getBody().userId());
        assertEquals(accessToken, response.getBody().accessToken());
        Assertions.assertTrue(response.getBody().errorMessage().isEmpty());
    }

    @Test
    void authenticateLogin_InvalidCredentials_ReturnsBadRequestStatus() {
        String username = "john";
        String password = "wrongpassword";

        when(emsUserRepository.findEMSUserByUsernameAndPassword(username, password)).thenReturn(null);

        KeycloakRequestService mockedKeycloakRequestService = mock(KeycloakRequestService.class);
        when(mockedKeycloakRequestService.makeKeycloakRequest(any(), any(), any())).thenReturn(ResponseEntity.badRequest().body("response"));
        authenticationServiceTest.setKeycloakRequestService(mockedKeycloakRequestService);

        ResponseEntity<AuthorizationDto> response = authenticationServiceTest.authenticateLogin(username, password);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody().userId());
        Assertions.assertTrue(response.getBody().accessToken().isEmpty());
        Assertions.assertFalse(response.getBody().errorMessage().isEmpty());
    }
}
