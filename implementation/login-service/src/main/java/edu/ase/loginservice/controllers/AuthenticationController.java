package edu.ase.loginservice.controllers;

import edu.ase.loginservice.dtos.AuthorizationDto;
import edu.ase.loginservice.dtos.RoleAccessVerificationDto;
import edu.ase.loginservice.services.impls.AuthenticationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/authenticate")
public class AuthenticationController {

    @Autowired
    private AuthenticationServiceImpl authenticationService;

    @CrossOrigin
    @GetMapping("/login")
    public ResponseEntity<AuthorizationDto> login(@RequestParam String username, @RequestParam String password) {
        return authenticationService.authenticateLogin(username, password);
    }

    @GetMapping(path = "/role-access")
    public ResponseEntity<RoleAccessVerificationDto> verifyRoleAccess(@RequestParam String accessToken, @RequestParam String endpoint) {
        return this.authenticationService.verifyRoleAccess(accessToken, endpoint);
    }

}
