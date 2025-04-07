package edu.ase.loginservice.exceptions;

public class KeycloakException extends RuntimeException {

    public KeycloakException(String errorMessage) {
        super("Keycloak Exception: " + errorMessage);
    }
}
