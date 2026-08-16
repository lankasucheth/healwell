package com.healwell.healwell_backend.controller;

import com.healwell.healwell_backend.model.SignupRequest;
import com.healwell.healwell_backend.model.Users;
import com.healwell.healwell_backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest request) {
        try {
            Users newUser = userService.registerPatient(request);
            return ResponseEntity.ok(newUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}