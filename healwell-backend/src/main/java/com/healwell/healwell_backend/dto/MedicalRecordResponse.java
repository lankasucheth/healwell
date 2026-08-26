package com.healwell.healwell_backend.dto;

import com.healwell.healwell_backend.model.MedicalRecord;
import java.time.LocalDateTime;

public class MedicalRecordResponse {

    private Long id;
    private Long appointmentId;
    private String diagnosis;
    private String prescription;
    private LocalDateTime recordDate;
    private DoctorSummary doctor;
    private PatientSummary patient;

    public MedicalRecordResponse(MedicalRecord record) {
        this.id = record.getId();
        this.appointmentId = record.getAppointment().getId();
        this.diagnosis = record.getDiagnosis();
        this.prescription = record.getPrescription();
        this.recordDate = record.getRecordDate();
        this.doctor = new DoctorSummary(record.getDoctor());
        this.patient = new PatientSummary(record.getPatient());
    }

    public Long getId() { return id; }
    public Long getAppointmentId() { return appointmentId; }
    public String getDiagnosis() { return diagnosis; }
    public String getPrescription() { return prescription; }
    public LocalDateTime getRecordDate() { return recordDate; }
    public DoctorSummary getDoctor() { return doctor; }
    public PatientSummary getPatient() { return patient; }

    public static class DoctorSummary {
        private String name;
        private String specialization;

        public DoctorSummary(com.healwell.healwell_backend.model.Doctor doctor) {
            this.name = doctor.getUser().getName();
            this.specialization = doctor.getSpecialization();
        }

        public String getName() { return name; }
        public String getSpecialization() { return specialization; }
    }

    public static class PatientSummary {
        private String name;

        public PatientSummary(com.healwell.healwell_backend.model.Patient patient) {
            this.name = patient.getUser().getName();
        }

        public String getName() { return name; }
    }
}
