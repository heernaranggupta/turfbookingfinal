package com.Turfbooking.models.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

@Valid
@Getter
@Setter
public class UpdateBookedTimeSlotRequest {

    @NotBlank
    private String bookingId;
    @NotNull
    private Double price;
    @NotBlank
    private String turfId;
    @NotBlank
    private String userId; // phone number (unique id) to know who booked this slot
    @NotBlank
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
}
