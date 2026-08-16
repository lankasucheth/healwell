package com.healwell.healwell_backend.controller;

import com.healwell.healwell_backend.model.Patient;
import com.healwell.healwell_backend.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        try {
            String email = authentication.getName();
            Patient patient = patientService.getPatientByEmail(email);
            return ResponseEntity.ok(patient);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(Authentication authentication, @RequestBody Patient updatedData) {
        try {
            String email = authentication.getName();
            Patient patient = patientService.updatePatientProfile(email, updatedData);
            return ResponseEntity.ok(patient);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/deactivate")
    public ResponseEntity<?> deactivate(Authentication authentication) {
        try {
            String email = authentication.getName();
            patientService.deactivateAccount(email);
            return ResponseEntity.ok("Account deactivated successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}