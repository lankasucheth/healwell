package com.healwell.healwell_backend.controller;

import com.healwell.healwell_backend.model.Appointment;
import com.healwell.healwell_backend.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import com.healwell.healwell_backend.dto.BookedSlotsResponse;
import com.healwell.healwell_backend.dto.AppointmentResponse;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals("ROLE_ADMIN"));
    }

    private boolean isDoctor(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals("ROLE_DOCTOR"));
    }

    @PostMapping("/book/{doctorId}")
    public ResponseEntity<?> bookAppointment(Authentication authentication,
                                              @PathVariable Long doctorId,
                                              @RequestBody Map<String, String> body) {
        try {
            String email = authentication.getName();
            LocalDateTime dateTime = LocalDateTime.parse(body.get("dateTime"));
            Appointment appointment = appointmentService.bookAppointment(email, doctorId, dateTime);
            return ResponseEntity.ok(appointment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/doctor/{doctorId}/booked-slots")
    public ResponseEntity<?> getBookedSlots(@PathVariable Long doctorId,
                                             @RequestParam String date) {
        try {
            LocalDate targetDate = LocalDate.parse(date);
            LocalDateTime start = targetDate.atStartOfDay();
            LocalDateTime end = targetDate.atTime(LocalTime.MAX);

            List<Appointment> appointments = appointmentService.getBookedAppointments(
                    doctorId, start, end, Appointment.Status.CANCELLED
            );

            List<LocalTime> bookedTimes = appointments.stream()
                    .map(a -> a.getDateTime().toLocalTime())
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new BookedSlotsResponse(bookedTimes));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/mine")
    public ResponseEntity<?> getMyAppointments(Authentication authentication) {
        try {
            String email = authentication.getName();
            List<Appointment> result;
            if (isDoctor(authentication)) {
                result = appointmentService.getMyAppointmentsAsDoctor(email);
            } else {
                result = appointmentService.getMyAppointmentsAsPatient(email);
            }
            List<AppointmentResponse> response = result.stream()
                    .map(AppointmentResponse::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllAppointments(Authentication authentication) {
        if (!isAdmin(authentication)) {
            return ResponseEntity.status(403).body("Only Admin can view all appointments");
        }
        return ResponseEntity.ok(appointmentService.getAllAppointments());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(Authentication authentication,
                                           @PathVariable Long id,
                                           @RequestBody Map<String, String> body) {
        try {
            String email = authentication.getName();
            boolean admin = isAdmin(authentication);
            Appointment.Status newStatus = Appointment.Status.valueOf(body.get("status"));
            Appointment updated = appointmentService.updateStatus(email, admin, id, newStatus);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelAppointment(Authentication authentication, @PathVariable Long id) {
        try {
            String email = authentication.getName();
            boolean admin = isAdmin(authentication);
            appointmentService.cancelAppointment(email, admin, id);
            return ResponseEntity.ok("Appointment cancelled successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}