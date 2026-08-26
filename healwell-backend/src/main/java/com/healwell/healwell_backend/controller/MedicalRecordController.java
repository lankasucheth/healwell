package com.healwell.healwell_backend.controller;

import com.healwell.healwell_backend.dto.MedicalRecordResponse;
import com.healwell.healwell_backend.model.MedicalRecord;
import com.healwell.healwell_backend.service.MedicalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/medical-records")
public class MedicalRecordController {

    @Autowired
    private MedicalRecordService medicalRecordService;

    private boolean isDoctor(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> "ROLE_DOCTOR".equals(a));
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
            return ResponseEntity.ok(new MedicalRecordResponse(record));
        } catch (AccessDeniedException e) {
            throw e;
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/mine")
    public ResponseEntity<?> getMyRecords(Authentication authentication) {
        try {
            String email = authentication.getName();
            List<MedicalRecord> records = medicalRecordService.getMyRecordsAsPatient(email);
            List<MedicalRecordResponse> response = records.stream()
                    .map(MedicalRecordResponse::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(response);
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
            List<MedicalRecordResponse> response = records.stream()
                    .map(MedicalRecordResponse::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (AccessDeniedException e) {
            throw e;
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDenied(AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }
}