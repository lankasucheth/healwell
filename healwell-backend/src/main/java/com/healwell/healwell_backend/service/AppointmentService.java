package com.healwell.healwell_backend.service;

import com.healwell.healwell_backend.model.*;
import com.healwell.healwell_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorAvailabilityRepository availabilityRepository;

    @Autowired
    private UserRepository userRepository;

    private Patient getPatientByEmail(String email) {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return patientRepository.findAll().stream()
                .filter(p -> p.getUser().getId().equals(user.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Patient profile not found"));
    }

    private Doctor getDoctorByEmail(String email) {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return doctorRepository.findAll().stream()
                .filter(d -> d.getUser().getId().equals(user.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Doctor profile not found"));
    }

    public Appointment bookAppointment(String patientEmail, Long doctorId, LocalDateTime requestedDateTime) {

        if (requestedDateTime.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Cannot book an appointment in the past");
        }

        Patient patient = getPatientByEmail(patientEmail);
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        DayOfWeek requestedDay = requestedDateTime.getDayOfWeek();
        LocalTime requestedTime = requestedDateTime.toLocalTime();

        List<DoctorAvailability> slots = availabilityRepository.findAll().stream()
                .filter(a -> a.getDoctor().getId().equals(doctorId))
                .filter(a -> a.getDayOfWeek().equals(requestedDay))
                .filter(DoctorAvailability::getIsAvailable)
                .toList();

        boolean withinAvailability = slots.stream().anyMatch(a ->
                !requestedTime.isBefore(a.getStartTime()) && requestedTime.isBefore(a.getEndTime()));

        if (!withinAvailability) {
            throw new RuntimeException("Doctor is not available at this day/time");
        }

        boolean alreadyBooked = appointmentRepository.findAll().stream()
                .anyMatch(a -> a.getDoctor().getId().equals(doctorId)
                        && a.getDateTime().equals(requestedDateTime)
                        && a.getStatus() != Appointment.Status.CANCELLED);

        if (alreadyBooked) {
            throw new RuntimeException("This slot is already booked");
        }

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setDateTime(requestedDateTime);
        appointment.setStatus(Appointment.Status.PENDING);

        return appointmentRepository.save(appointment);
    }

    public List<Appointment> getMyAppointmentsAsPatient(String patientEmail) {
        Patient patient = getPatientByEmail(patientEmail);
        return appointmentRepository.findAll().stream()
                .filter(a -> a.getPatient().getId().equals(patient.getId()))
                .toList();
    }

    public List<Appointment> getMyAppointmentsAsDoctor(String doctorEmail) {
        Doctor doctor = getDoctorByEmail(doctorEmail);
        return appointmentRepository.findAll().stream()
                .filter(a -> a.getDoctor().getId().equals(doctor.getId()))
                .toList();
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public Appointment updateStatus(String requesterEmail, boolean isAdmin, Long appointmentId, Appointment.Status newStatus) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (!isAdmin) {
            Users user = userRepository.findByEmail(requesterEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            boolean isOwnerPatient = appointment.getPatient().getUser().getId().equals(user.getId());
            boolean isOwnerDoctor = appointment.getDoctor().getUser().getId().equals(user.getId());

            if (!isOwnerPatient && !isOwnerDoctor) {
                throw new RuntimeException("You can only update your own appointments");
            }
        }

        appointment.setStatus(newStatus);
        return appointmentRepository.save(appointment);
    }

    public void cancelAppointment(String requesterEmail, boolean isAdmin, Long appointmentId) {
        updateStatus(requesterEmail, isAdmin, appointmentId, Appointment.Status.CANCELLED);
    }
}