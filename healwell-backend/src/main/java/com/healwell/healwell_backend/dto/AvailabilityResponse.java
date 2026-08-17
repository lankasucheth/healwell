package com.healwell.healwell_backend.dto;

import com.healwell.healwell_backend.model.DoctorAvailability;
import java.time.DayOfWeek;
import java.time.LocalTime;

public class AvailabilityResponse {

    private Long id;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer slotDurationMinutes;
    private Boolean isAvailable;

    public AvailabilityResponse(DoctorAvailability slot) {
        this.id = slot.getId();
        this.dayOfWeek = slot.getDayOfWeek();
        this.startTime = slot.getStartTime();
        this.endTime = slot.getEndTime();
        this.slotDurationMinutes = slot.getSlotDurationMinutes();
        this.isAvailable = slot.getIsAvailable();
    }

    public Long getId() { return id; }
    public DayOfWeek getDayOfWeek() { return dayOfWeek; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }
    public Integer getSlotDurationMinutes() { return slotDurationMinutes; }
    public Boolean getIsAvailable() { return isAvailable; }
}