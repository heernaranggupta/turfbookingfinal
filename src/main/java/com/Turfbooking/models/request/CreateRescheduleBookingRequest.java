package com.Turfbooking.models.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Valid
public class CreateRescheduleBookingRequest {

    @NotNull
    private String bookingId;
    @NotNull
    private Integer slotNumber;
    @NotNull
    private String turfId;
    @NotNull
    private String userId; // for business phone number (unique id) to know who booked this slot
    @NotNull
    private LocalDate date;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

}
