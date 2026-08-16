package com.healwell.healwell_backend.service;

import com.healwell.healwell_backend.model.Doctor;
import com.healwell.healwell_backend.model.DoctorAvailability;
import com.healwell.healwell_backend.model.Users;
import com.healwell.healwell_backend.repository.DoctorAvailabilityRepository;
import com.healwell.healwell_backend.repository.DoctorRepository;
import com.healwell.healwell_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorAvailabilityService {

    @Autowired
    private DoctorAvailabilityRepository availabilityRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private UserRepository userRepository;

    private Doctor getDoctorByOwnEmail(String email) {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return doctorRepository.findAll().stream()
                .filter(d -> d.getUser().getId().equals(user.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Doctor profile not found"));
    }

    public DoctorAvailability addAvailability(String requesterEmail, boolean isAdmin, Long doctorId, DoctorAvailability newSlot) {
        Doctor doctor;
        if (isAdmin) {
            doctor = doctorRepository.findById(doctorId)
                    .orElseThrow(() -> new RuntimeException("Doctor not found"));
        } else {
            doctor = getDoctorByOwnEmail(requesterEmail);
        }

        newSlot.setDoctor(doctor);
        if (newSlot.getIsAvailable() == null) {
            newSlot.setIsAvailable(true);
        }
        return availabilityRepository.save(newSlot);
    }

    public List<DoctorAvailability> getAvailabilityByDoctorId(Long doctorId) {
        return availabilityRepository.findAll().stream()
                .filter(a -> a.getDoctor().getId().equals(doctorId))
                .toList();
    }

    public void deleteAvailability(String requesterEmail, boolean isAdmin, Long availabilityId) {
        DoctorAvailability slot = availabilityRepository.findById(availabilityId)
                .orElseThrow(() -> new RuntimeException("Availability slot not found"));

        if (!isAdmin) {
            Doctor ownDoctor = getDoctorByOwnEmail(requesterEmail);
            if (!slot.getDoctor().getId().equals(ownDoctor.getId())) {
                throw new RuntimeException("You can only delete your own availability slots");
            }
        }

        availabilityRepository.delete(slot);
    }
}