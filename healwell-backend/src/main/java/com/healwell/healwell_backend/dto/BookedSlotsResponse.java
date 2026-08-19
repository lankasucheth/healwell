package com.healwell.healwell_backend.dto;

import java.time.LocalTime;
import java.util.List;

public class BookedSlotsResponse {

    private List<LocalTime> bookedTimes;

    public BookedSlotsResponse(List<LocalTime> bookedTimes) {
        this.bookedTimes = bookedTimes;
    }

    public List<LocalTime> getBookedTimes() {
        return bookedTimes;
    }
}