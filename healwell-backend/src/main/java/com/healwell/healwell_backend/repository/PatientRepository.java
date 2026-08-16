package com.healwell.healwell_backend.repository;

import com.healwell.healwell_backend.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {
}