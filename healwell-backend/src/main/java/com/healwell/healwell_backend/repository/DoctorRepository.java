package com.healwell.healwell_backend.repository;

import com.healwell.healwell_backend.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
}