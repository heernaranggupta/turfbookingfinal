package com.Turfbooking.models.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Valid
public class CreateRescheduleBookingRequest {

    @NotNull
    private String bookingId;
    @NotNull
    private String turfId;
    @NotNull
    private String userId; // for business phone number (unique id) to know who booked this slot
    @NotNull
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

}
