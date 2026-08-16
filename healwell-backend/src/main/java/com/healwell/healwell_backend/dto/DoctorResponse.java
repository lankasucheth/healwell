package com.healwell.healwell_backend.dto;

import com.healwell.healwell_backend.model.Doctor;
import java.math.BigDecimal;

public class DoctorResponse {

    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private String specialization;
    private String qualification;
    private Integer experienceYears;
    private BigDecimal consultationFee;
    private String bio;

    public DoctorResponse(Doctor doctor) {
        this.id = doctor.getId();
        this.name = doctor.getUser().getName();
        this.email = doctor.getUser().getEmail();
        this.phoneNumber = doctor.getUser().getPhoneNumber();
        this.specialization = doctor.getSpecialization();
        this.qualification = doctor.getQualification();
        this.experienceYears = doctor.getExperienceYears();
        this.consultationFee = doctor.getConsultationFee();
        this.bio = doctor.getBio();
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getSpecialization() { return specialization; }
    public String getQualification() { return qualification; }
    public Integer getExperienceYears() { return experienceYears; }
    public BigDecimal getConsultationFee() { return consultationFee; }
    public String getBio() { return bio; }
}