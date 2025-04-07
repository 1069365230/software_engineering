package edu.ase.loginservice.dtos;

public record RoleAccessVerificationDto(String role, boolean isVerified, String endpoint) {
}