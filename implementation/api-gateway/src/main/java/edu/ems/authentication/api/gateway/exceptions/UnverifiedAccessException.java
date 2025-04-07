package edu.ems.authentication.api.gateway.exceptions;

import edu.ems.authentication.api.gateway.dtos.RoleAccessVerificationDto;

public class UnverifiedAccessException extends RuntimeException {
    public UnverifiedAccessException(RoleAccessVerificationDto roleAccessVerificationDto) {
        super(produceErrorMessage(roleAccessVerificationDto));
    }

    private static String produceErrorMessage(RoleAccessVerificationDto roleAccessVerificationDto) {
        return String.format("Role is not verified for endpoint %s", roleAccessVerificationDto.endpoint());
    }
}
