package com.healwell.healwell_backend.controller;

import com.healwell.healwell_backend.model.DoctorAvailability;
import com.healwell.healwell_backend.service.DoctorAvailabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/availability")
public class DoctorAvailabilityController {

    @Autowired
    private DoctorAvailabilityService availabilityService;

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals("ROLE_ADMIN"));
    }

    @PostMapping("/{doctorId}")
    public ResponseEntity<?> addAvailability(Authentication authentication,
                                              @PathVariable Long doctorId,
                                              @RequestBody DoctorAvailability newSlot) {
        try {
            String email = authentication.getName();
            boolean admin = isAdmin(authentication);
            DoctorAvailability saved = availabilityService.addAvailability(email, admin, doctorId, newSlot);
            return ResponseEntity.ok(saved);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{doctorId}")
    public ResponseEntity<List<DoctorAvailability>> getAvailability(@PathVariable Long doctorId) {
        return ResponseEntity.ok(availabilityService.getAvailabilityByDoctorId(doctorId));
    }

    @DeleteMapping("/{availabilityId}")
    public ResponseEntity<?> deleteAvailability(Authentication authentication, @PathVariable Long availabilityId) {
        try {
            String email = authentication.getName();
            boolean admin = isAdmin(authentication);
            availabilityService.deleteAvailability(email, admin, availabilityId);
            return ResponseEntity.ok("Availability slot deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}