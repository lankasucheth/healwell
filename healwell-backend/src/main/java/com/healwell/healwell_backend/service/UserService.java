package com.healwell.healwell_backend.service;

import com.healwell.healwell_backend.model.Patient;
import com.healwell.healwell_backend.model.SignupRequest;
import com.healwell.healwell_backend.model.Users;
import com.healwell.healwell_backend.repository.PatientRepository;
import com.healwell.healwell_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Users registerPatient(SignupRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        Users user = new Users();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Users.Role.PATIENT);
        user.setIsActive(true);

        Users savedUser = userRepository.save(user);

        Patient patient = new Patient();
        patient.setUser(savedUser);
        patientRepository.save(patient);

        return savedUser;
    }
}