package edu.ase.loginservice.testserviceimpls;

import edu.ase.loginservice.services.helpers.KeycloakRequestService;
import edu.ase.loginservice.services.impls.AuthenticationServiceImpl;

public class AuthenticationServiceTestImpl extends AuthenticationServiceImpl {
    public void setKeycloakRequestService(KeycloakRequestService keycloakRequestService) {
        this.keycloakRequestService = keycloakRequestService;
    }
}
