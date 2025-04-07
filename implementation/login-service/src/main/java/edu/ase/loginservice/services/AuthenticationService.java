package edu.ase.loginservice.services;

import edu.ase.loginservice.dtos.AuthorizationDto;
import edu.ase.loginservice.dtos.RoleAccessVerificationDto;
import org.springframework.http.ResponseEntity;

public interface AuthenticationService {
    ResponseEntity<AuthorizationDto> authenticateLogin(String username, String password);

    ResponseEntity<RoleAccessVerificationDto> verifyRoleAccess(String accessToken, String endpoint);
}
