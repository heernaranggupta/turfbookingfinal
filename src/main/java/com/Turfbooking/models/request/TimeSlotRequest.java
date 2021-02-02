package com.Turfbooking.models.request;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Valid
public class TimeSlotRequest {

    @NotEmpty
    private String turfId;
    private Double price;
    private LocalDate date;
    @NotEmpty
    private LocalTime startTime;
    private LocalTime endTime;

}
