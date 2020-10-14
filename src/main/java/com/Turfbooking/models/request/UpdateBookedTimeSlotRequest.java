package com.Turfbooking.models.request;

import lombok.Getter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Valid
@Getter
public class UpdateBookedTimeSlotRequest {

    @NotNull
    private String bookingId;
    @NotNull
    private Integer slotNumber;
    @NotNull
    private String turfId;
    @NotNull
    private String userId; // phone number (unique id) to know who booked this slot
    @NotNull
    private LocalDate date;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
