package edu.ase.loginservice.services.impls;

import edu.ase.loginservice.dtos.AuthorizationDto;
import edu.ase.loginservice.dtos.RoleAccessVerificationDto;
import edu.ase.loginservice.models.EMSUser;
import edu.ase.loginservice.repositories.EMSUserRepository;
import edu.ase.loginservice.services.AuthenticationService;
import edu.ase.loginservice.services.helpers.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private RoleMappingService roleMappingService;

    @Autowired
    protected KeycloakRequestService keycloakRequestService;

    @Autowired
    private EMSUserRepository emsUserRepository;

    private static final Logger log = LoggerFactory.getLogger(AuthenticationServiceImpl.class);


    @Override
    public ResponseEntity<RoleAccessVerificationDto> verifyRoleAccess(String accessToken, String endpoint) {
        Optional<List<String>> userRoles = jwtService.verifyAccessToken(accessToken);

        if (userRoles.isPresent()) {
            String role = roleMappingService.verifyRoleAccess(userRoles.get(), endpoint);
            if (role != null) {
                log.info(role + " was verified for " + endpoint);
                return ResponseEntity.ok(new RoleAccessVerificationDto(role, true, endpoint));
            } else {
                return ResponseEntity.ok(new RoleAccessVerificationDto(null, false, endpoint));
            }

        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


    @Override
    public ResponseEntity<AuthorizationDto> authenticateLogin(String username, String password) {

        KeycloakRequest keycloakRequest = new KeycloakRequest.Builder(MediaType.APPLICATION_FORM_URLENCODED)
                .addCustomKeycloakFormBody("account", username, password)
                .build();

        ResponseEntity<String> response = this.keycloakRequestService.makeKeycloakRequest(KeycloakRequestType.Authenticate, keycloakRequest, null);

        EMSUser emsUser = this.emsUserRepository.findEMSUserByUsernameAndPassword(username, password);

        if (emsUser == null) {
            String errorMessage = username + " failed to login at " + Instant.now() + " because of faulty credentials!";
            log.info(errorMessage);
            return ResponseEntity.badRequest().body(new AuthorizationDto(null, "", "", "Keycloak error"));
        }


        if (response == null || response.getBody() == null)
            return ResponseEntity.badRequest().body(new AuthorizationDto(null, "", "", ""));

        log.info(username + " logged in at " + Instant.now());
        return ResponseEntity.ofNullable(
                new AuthorizationDto(emsUser.getId(), emsUser.getRole(), this.keycloakRequestService.fromKeycloakResponseJson(response.getBody(), "access_token"), "")
        );
    }
}
