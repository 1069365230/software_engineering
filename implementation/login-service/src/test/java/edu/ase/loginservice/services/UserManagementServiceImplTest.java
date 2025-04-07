package edu.ase.loginservice.services;

import edu.ase.loginservice.dtos.CreateEMSUserDto;
import edu.ase.loginservice.models.EMSUser;
import edu.ase.loginservice.repositories.EMSUserRepository;
import edu.ase.loginservice.services.helpers.KeycloakRequest;
import edu.ase.loginservice.services.helpers.KeycloakRequestService;
import edu.ase.loginservice.services.helpers.KeycloakRequestType;
import edu.ase.loginservice.services.impls.UserManagementServiceImpl;
import edu.ase.loginservice.stream.StreamProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class UserManagementServiceImplTest {

    @InjectMocks
    private UserManagementServiceImpl userManagementService;

    @Mock
    private StreamProducer streamProducer;

    @Mock
    private EMSUserRepository emsUserRepository;

    @Mock
    private KeycloakRequestService keycloakRequestService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        String token = "access_token";
        when(keycloakRequestService.makeKeycloakRequest(eq(KeycloakRequestType.Authenticate), any(), isNull()))
                .thenReturn(new ResponseEntity<>(jsonContainingToken(token), HttpStatus.OK));
        when(keycloakRequestService.fromKeycloakResponseJson(anyString(), eq("access_token")))
                .thenReturn(token);

        when(keycloakRequestService.makeKeycloakRequest(eq(KeycloakRequestType.CreateUser), any(), isNull()))
                .thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    private String jsonContainingToken(String token) {
        return "{\"access_token\": \"" + token + "\"}";
    }

    @Test
    public void testCreateUser() {
        CreateEMSUserDto createEMSUserDto = new CreateEMSUserDto(
                "test",
                "test@test.at",
                "test",
                "test",
                "test",
                "TT",
                'x',
                "test",
                "testRole"
        );

        when(keycloakRequestService.makeKeycloakRequest(eq(KeycloakRequestType.CreateUser), any(KeycloakRequest.class), isNull()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        when(keycloakRequestService.makeKeycloakRequest(eq(KeycloakRequestType.SetRole), any(KeycloakRequest.class), eq(createEMSUserDto.username())))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        EMSUser expectedUser = new EMSUser(
                "test",
                "test@test.at",
                "test",
                "test",
                "test",
                "TT",
                'x',
                "test",
                "testRole");

        when(emsUserRepository.save(any(EMSUser.class))).thenReturn(expectedUser);

        userManagementService.createUser(createEMSUserDto);

        verify(emsUserRepository, times(1)).save(expectedUser);
        verify(streamProducer, times(1)).publishUser(expectedUser);
    }

    @Test
    public void testDeleteUser() {
        String username = "test";
        EMSUser userToDelete = new EMSUser(
                username,
                "test@test.at",
                "test",
                "test",
                "test",
                "TT",
                'x',
                "test",
                "testRole"
        );

        when(emsUserRepository.findEMSUserByUsername(username)).thenReturn(userToDelete);
        when(keycloakRequestService.makeKeycloakRequest(eq(KeycloakRequestType.DeleteUser), any(KeycloakRequest.class), eq(username)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        userManagementService.deleteUser(username);

        verify(emsUserRepository, times(1)).findEMSUserByUsername(username);
        verify(keycloakRequestService, times(1)).makeKeycloakRequest(eq(KeycloakRequestType.DeleteUser), any(KeycloakRequest.class), eq(username));
        verify(emsUserRepository, times(1)).delete(userToDelete);
    }

}