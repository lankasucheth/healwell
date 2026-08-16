package com.healwell.healwell_backend.repository;

import com.healwell.healwell_backend.model.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
}