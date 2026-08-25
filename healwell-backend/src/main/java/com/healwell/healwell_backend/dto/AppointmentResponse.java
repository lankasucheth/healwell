package com.healwell.healwell_backend.dto;

import com.healwell.healwell_backend.model.Appointment;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AppointmentResponse {

    private Long id;
    private LocalDateTime dateTime;
    private Appointment.Status status;
    private DoctorSummary doctor;
    private PatientSummary patient;

    public AppointmentResponse(Appointment appointment) {
        this.id = appointment.getId();
        this.dateTime = appointment.getDateTime();
        this.status = appointment.getStatus();
        this.doctor = new DoctorSummary(appointment.getDoctor());
        this.patient = new PatientSummary(appointment.getPatient());
    }

    public Long getId() { return id; }
    public LocalDateTime getDateTime() { return dateTime; }
    public Appointment.Status getStatus() { return status; }
    public DoctorSummary getDoctor() { return doctor; }
    public PatientSummary getPatient() { return patient; }

    public static class DoctorSummary {
        private String name;
        private String specialization;
        private BigDecimal consultationFee;

        public DoctorSummary(com.healwell.healwell_backend.model.Doctor doctor) {
            this.name = doctor.getUser().getName();
            this.specialization = doctor.getSpecialization();
            this.consultationFee = doctor.getConsultationFee();
        }

        public String getName() { return name; }
        public String getSpecialization() { return specialization; }
        public BigDecimal getConsultationFee() { return consultationFee; }
    }

    public static class PatientSummary {
        private String name;

        public PatientSummary(com.healwell.healwell_backend.model.Patient patient) {
            this.name = patient.getUser().getName();
        }

        public String getName() { return name; }
    }
}
