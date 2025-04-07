package edu.ase.loginservice.services.impls;

import edu.ase.loginservice.dtos.CreateEMSUserDto;
import edu.ase.loginservice.dtos.KeycloakRoleDto;
import edu.ase.loginservice.models.EMSUser;
import edu.ase.loginservice.repositories.EMSUserRepository;
import edu.ase.loginservice.services.UserManagementService;
import edu.ase.loginservice.services.helpers.KeycloakRequest;
import edu.ase.loginservice.services.helpers.KeycloakRequestService;
import edu.ase.loginservice.services.helpers.KeycloakRequestType;
import edu.ase.loginservice.stream.StreamProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserManagementServiceImpl implements UserManagementService {
    @Autowired
    protected StreamProducer streamProducer;

    @Autowired
    private EMSUserRepository emsUserRepository;

    @Autowired
    private KeycloakRequestService keycloakRequestService;

    private static final Logger log = LoggerFactory.getLogger(UserManagementServiceImpl.class);

    @Override
    public ResponseEntity<String> createUser(CreateEMSUserDto requestBody) {
        if (requestBody.role() == null || requestBody.role().isEmpty())
            return ResponseEntity.badRequest().body("Undefined role");

        KeycloakRequest keycloakRequest = new KeycloakRequest.Builder(MediaType.APPLICATION_JSON)
                .addAdminAuthorizationBearerToken(this.keycloakRequestService)
                .addCreateUserBody(requestBody)
                .build();

        ResponseEntity<String> response = this.keycloakRequestService.makeKeycloakRequest(KeycloakRequestType.CreateUser, keycloakRequest, null);

        if (response.getStatusCode().is2xxSuccessful()) {

            KeycloakRequest setRoleRequest = new KeycloakRequest.Builder(MediaType.APPLICATION_JSON)
                    .addAdminAuthorizationBearerToken(this.keycloakRequestService)
                    .addJsonBody(List.of(new KeycloakRoleDto(this.keycloakRequestService.getRoleIdByName(requestBody.role()), requestBody.role())))
                    .build();

            ResponseEntity<String> setRoleResponse = this.keycloakRequestService.makeKeycloakRequest(KeycloakRequestType.SetRole, setRoleRequest, requestBody.username());

            EMSUser user = new EMSUser(
                    requestBody.username(),
                    requestBody.email(),
                    requestBody.password(),
                    requestBody.forename(),
                    requestBody.surname(),
                    requestBody.countryCode(),
                    requestBody.gender(),
                    requestBody.hometown(),
                    (requestBody.role() == null || requestBody.role().isEmpty() || requestBody.role().equals("null")) ? "attendee" : requestBody.role()
            );

            if (setRoleResponse.getStatusCode().is2xxSuccessful()) {
                try {
                    emsUserRepository.save(user);
                } catch (Exception e) {
                    log.error("Could not save user...", e);
                    this.deleteUser(user.getUsername());
                    return ResponseEntity.badRequest().body(setRoleResponse.getBody());
                }
                this.streamProducer.publishUser(user);
            } else {
                log.error("Bad Keycloak Request at " + this.getClass().getName());
                log.error(setRoleResponse.getBody());
                this.deleteUser(user.getUsername());
                return ResponseEntity.badRequest().body(setRoleResponse.getBody());
            }

            return ResponseEntity.ok(user.toString());
        } else {
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        }
    }

    @Override
    public ResponseEntity<String> deleteUser(String username) {

        KeycloakRequest keycloakRequest = new KeycloakRequest.Builder(MediaType.APPLICATION_JSON)
                .addAdminAuthorizationBearerToken(this.keycloakRequestService)
                .build();

        ResponseEntity<String> response = keycloakRequestService.makeKeycloakRequest(KeycloakRequestType.DeleteUser, keycloakRequest, username);
        EMSUser emsUser = this.emsUserRepository.findEMSUserByUsername(username);
        this.emsUserRepository.delete(emsUser);

        if (response.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        }
    }


}
