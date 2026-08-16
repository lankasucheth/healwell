package com.healwell.healwell_backend.service;

import com.healwell.healwell_backend.model.*;
import com.healwell.healwell_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicalRecordService {

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private UserRepository userRepository;

    private Doctor getDoctorByEmail(String email) {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return doctorRepository.findAll().stream()
                .filter(d -> d.getUser().getId().equals(user.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Doctor profile not found"));
    }

    private Patient getPatientByEmail(String email) {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return patientRepository.findAll().stream()
                .filter(p -> p.getUser().getId().equals(user.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Patient profile not found"));
    }

    public MedicalRecord createRecord(String doctorEmail, Long appointmentId, String diagnosis, String prescription) {
        Doctor doctor = getDoctorByEmail(doctorEmail);

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (!appointment.getDoctor().getId().equals(doctor.getId())) {
            throw new RuntimeException("You can only create records for your own appointments");
        }

        MedicalRecord record = new MedicalRecord();
        record.setPatient(appointment.getPatient());
        record.setDoctor(doctor);
        record.setAppointment(appointment);
        record.setDiagnosis(diagnosis);
        record.setPrescription(prescription);

        MedicalRecord saved = medicalRecordRepository.save(record);

        appointment.setStatus(Appointment.Status.COMPLETED);
        appointmentRepository.save(appointment);

        return saved;
    }

    public List<MedicalRecord> getMyRecordsAsPatient(String patientEmail) {
        Patient patient = getPatientByEmail(patientEmail);
        return medicalRecordRepository.findAll().stream()
                .filter(r -> r.getPatient().getId().equals(patient.getId()))
                .sorted((a, b) -> b.getRecordDate().compareTo(a.getRecordDate()))
                .toList();
    }

    public List<MedicalRecord> getPatientRecordsAsDoctor(String doctorEmail, Long patientId) {
        getDoctorByEmail(doctorEmail);
        return medicalRecordRepository.findAll().stream()
                .filter(r -> r.getPatient().getId().equals(patientId))
                .sorted((a, b) -> b.getRecordDate().compareTo(a.getRecordDate()))
                .toList();
    }
}