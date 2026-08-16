package com.healwell.healwell_backend.model;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class DoctorCreateRequest {

    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String email;

    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be exactly 10 digits")
    private String phoneNumber;

    @NotBlank
    private String password;

    @NotBlank
    private String specialization;

    @NotBlank
    private String qualification;

    @NotNull
    private Integer experienceYears;

    @NotNull
    private BigDecimal consultationFee;

    @NotBlank
    private String bio;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    public String getQualification() { return qualification; }
    public void setQualification(String qualification) { this.qualification = qualification; }
    public Integer getExperienceYears() { return experienceYears; }
    public void setExperienceYears(Integer experienceYears) { this.experienceYears = experienceYears; }
    public BigDecimal getConsultationFee() { return consultationFee; }
    public void setConsultationFee(BigDecimal consultationFee) { this.consultationFee = consultationFee; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
}