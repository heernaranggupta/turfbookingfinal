package com.Turfbooking.models.request;

import lombok.Getter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Valid
@Getter
public class UpdateBookedTimeSlotRequest {

    @NotBlank
    private String bookingId;
    @NotBlank
    private Integer slotNumber;
    @NotNull
    private Double price;
    @NotBlank
    private String turfId;
    @NotBlank
    private String userId; // phone number (unique id) to know who booked this slot
    @NotBlank
    private LocalDate date;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}