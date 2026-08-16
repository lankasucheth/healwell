package com.healwell.healwell_backend.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class SignupRequest {

    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String email;

    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be exactly 10 digits")
    private String phoneNumber;

    @NotBlank
    private String password;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}