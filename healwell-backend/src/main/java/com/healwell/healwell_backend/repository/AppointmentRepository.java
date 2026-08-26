package com.healwell.healwell_backend.repository;

import com.healwell.healwell_backend.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    boolean existsByDoctorIdAndPatientIdAndStatus(Long doctorId, Long patientId, Appointment.Status status);
}