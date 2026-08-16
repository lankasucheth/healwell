package com.healwell.healwell_backend.service;

import com.healwell.healwell_backend.model.Doctor;
import com.healwell.healwell_backend.model.DoctorCreateRequest;
import com.healwell.healwell_backend.model.Users;
import com.healwell.healwell_backend.repository.DoctorRepository;
import com.healwell.healwell_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Doctor createDoctor(DoctorCreateRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        Users user = new Users();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Users.Role.DOCTOR);
        user.setIsActive(true);

        Users savedUser = userRepository.save(user);

        Doctor doctor = new Doctor();
        doctor.setUser(savedUser);
        doctor.setSpecialization(request.getSpecialization());
        doctor.setQualification(request.getQualification());
        doctor.setExperienceYears(request.getExperienceYears());
        doctor.setConsultationFee(request.getConsultationFee());
        doctor.setBio(request.getBio());

        return doctorRepository.save(doctor);
    }

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public Doctor getDoctorById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
    }

    public Doctor updateDoctor(Long id, Doctor updatedData) {
        Doctor doctor = getDoctorById(id);

        doctor.setSpecialization(updatedData.getSpecialization());
        doctor.setQualification(updatedData.getQualification());
        doctor.setExperienceYears(updatedData.getExperienceYears());
        doctor.setConsultationFee(updatedData.getConsultationFee());
        doctor.setBio(updatedData.getBio());

        return doctorRepository.save(doctor);
    }

    public void deleteDoctor(Long id) {
        Doctor doctor = getDoctorById(id);
        doctorRepository.delete(doctor);
        userRepository.delete(doctor.getUser());
    }
}