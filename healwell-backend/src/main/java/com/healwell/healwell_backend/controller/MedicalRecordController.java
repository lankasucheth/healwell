package com.healwell.healwell_backend.controller;

import com.healwell.healwell_backend.model.MedicalRecord;
import com.healwell.healwell_backend.service.MedicalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/medical-records")
public class MedicalRecordController {

    @Autowired
    private MedicalRecordService medicalRecordService;

    private boolean isDoctor(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals("ROLE_DOCTOR"));
    }

    @PostMapping("/{appointmentId}")
    public ResponseEntity<?> createRecord(Authentication authentication,
                                           @PathVariable Long appointmentId,
                                           @RequestBody Map<String, String> body) {
        if (!isDoctor(authentication)) {
            return ResponseEntity.status(403).body("Only Doctors can create medical records");
        }
        try {
            String email = authentication.getName();
            MedicalRecord record = medicalRecordService.createRecord(
                    email, appointmentId, body.get("diagnosis"), body.get("prescription"));
            return ResponseEntity.ok(record);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/mine")
    public ResponseEntity<?> getMyRecords(Authentication authentication) {
        try {
            String email = authentication.getName();
            List<MedicalRecord> records = medicalRecordService.getMyRecordsAsPatient(email);
            return ResponseEntity.ok(records);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<?> getPatientRecords(Authentication authentication, @PathVariable Long patientId) {
        if (!isDoctor(authentication)) {
            return ResponseEntity.status(403).body("Only Doctors can view patient history this way");
        }
        try {
            String email = authentication.getName();
            List<MedicalRecord> records = medicalRecordService.getPatientRecordsAsDoctor(email, patientId);
            return ResponseEntity.ok(records);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}