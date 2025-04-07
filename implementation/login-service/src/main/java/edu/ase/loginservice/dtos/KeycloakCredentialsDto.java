package edu.ase.loginservice.dtos;

public record KeycloakCredentialsDto(String type, String value, boolean temporary) {
}
