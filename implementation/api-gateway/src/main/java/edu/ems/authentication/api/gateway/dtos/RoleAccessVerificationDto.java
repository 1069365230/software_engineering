package edu.ems.authentication.api.gateway.dtos;

public record RoleAccessVerificationDto(String role, boolean isVerified, String endpoint) {
}