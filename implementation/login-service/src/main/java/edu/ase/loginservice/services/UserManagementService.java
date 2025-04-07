package edu.ase.loginservice.services;

import edu.ase.loginservice.dtos.CreateEMSUserDto;
import org.springframework.http.ResponseEntity;

public interface UserManagementService {
    ResponseEntity<String> createUser(CreateEMSUserDto requestBody);

    ResponseEntity<String> deleteUser(String userId);
}
