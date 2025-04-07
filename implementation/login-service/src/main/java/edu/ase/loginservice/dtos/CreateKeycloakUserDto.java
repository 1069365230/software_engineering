package edu.ase.loginservice.dtos;

import java.util.List;

public record CreateKeycloakUserDto(String username,
                                    String email,
                                    String firstName,
                                    String lastName,
                                    boolean enabled,
                                    boolean emailVerified,
                                    List<KeycloakCredentialsDto> credentials) {
}
