package edu.ase.loginservice.dtos;

public record AuthorizationDto(Long userId, String role, String accessToken, String errorMessage) {
}
