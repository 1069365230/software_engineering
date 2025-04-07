package edu.ase.loginservice.services;

import edu.ase.loginservice.dtos.SystemHealthDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface MaintenanceViewService {
    ResponseEntity<List<SystemHealthDto>> getSystemHealth();
}
