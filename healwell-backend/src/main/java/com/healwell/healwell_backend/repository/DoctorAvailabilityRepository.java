package com.healwell.healwell_backend.repository;

import com.healwell.healwell_backend.model.DoctorAvailability;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorAvailabilityRepository extends JpaRepository<DoctorAvailability, Long> {
}