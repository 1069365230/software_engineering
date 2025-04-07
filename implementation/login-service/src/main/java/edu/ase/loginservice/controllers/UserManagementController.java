package edu.ase.loginservice.controllers;

import edu.ase.loginservice.dtos.CreateEMSUserDto;
import edu.ase.loginservice.services.impls.UserManagementServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@CrossOrigin
@RequestMapping("/management")
public class UserManagementController {

    @Autowired
    private UserManagementServiceImpl userManagementService;

    @PostMapping(path = "/users", consumes = "application/json")
    public ResponseEntity createUser(@RequestBody CreateEMSUserDto requestBody) {
        return userManagementService.createUser(requestBody);
    }

    @DeleteMapping(path = "/users/{username}")
    public ResponseEntity deleteUser(@PathVariable("username") String username) {
        return userManagementService.deleteUser(username);
    }
}
