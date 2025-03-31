package com.infirmary.backend.configuration.controller;

import com.infirmary.backend.configuration.dto.UserDTO;
import com.infirmary.backend.configuration.model.Patient;
import com.infirmary.backend.configuration.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{email}")
    public Patient getUser(@PathVariable String email) {
        return userService.getUserByEmail(email);
    }

    @GetMapping("/users")
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }
    
    

    @PostMapping
    public UserDTO createUser(@RequestBody UserDTO dto) {
        return userService.createUser(dto);
    }

    @PutMapping("/{id}")
    public UserDTO updateUser(@PathVariable String id, @RequestBody UserDTO dto) {
        return userService.updateUser(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        System.out.println("Deleting user: " + id); // debug log
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }    
}
