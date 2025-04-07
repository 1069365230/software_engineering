package edu.ase.loginservice.dtos;

public record CreateEMSUserDto(String username,
                               String email,
                               String password,
                               String forename,
                               String surname,
                               String countryCode,
                               char gender,
                               String hometown,
                               String role) {
}
