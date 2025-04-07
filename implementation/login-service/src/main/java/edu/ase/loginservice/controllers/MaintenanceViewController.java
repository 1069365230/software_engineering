package edu.ase.loginservice.controllers;

import edu.ase.loginservice.dtos.SystemHealthDto;
import edu.ase.loginservice.services.impls.MaintenanceViewServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/maintenance")
public class MaintenanceViewController {

    @Autowired
    private MaintenanceViewServiceImpl maintenanceViewService;

    @GetMapping("/health")
    public ResponseEntity<List<SystemHealthDto>> getSystemHealth() {
        return maintenanceViewService.getSystemHealth();
    }
}
