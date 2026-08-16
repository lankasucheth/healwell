package com.healwell.healwell_backend.service;

import com.healwell.healwell_backend.model.Patient;
import com.healwell.healwell_backend.model.Users;
import com.healwell.healwell_backend.repository.PatientRepository;
import com.healwell.healwell_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private UserRepository userRepository;

    public Patient getPatientByEmail(String email) {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return patientRepository.findAll().stream()
                .filter(p -> p.getUser().getId().equals(user.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Patient profile not found"));
    }

    public Patient updatePatientProfile(String email, Patient updatedData) {
        Patient patient = getPatientByEmail(email);

        patient.setDateOfBirth(updatedData.getDateOfBirth());
        patient.setGender(updatedData.getGender());
        patient.setAddress(updatedData.getAddress());
        patient.setBloodGroup(updatedData.getBloodGroup());

        return patientRepository.save(patient);
    }

    public void deactivateAccount(String email) {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsActive(false);
        userRepository.save(user);
    }
}