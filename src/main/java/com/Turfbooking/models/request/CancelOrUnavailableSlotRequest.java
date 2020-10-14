package com.Turfbooking.models.request;

import lombok.Getter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Valid
@Getter
public class CancelOrUnavailableSlotRequest {

    private String bookingId;
    @NotNull
    private String turfId;
    @NotNull
    private LocalDate date;
    @NotNull
    private Integer slotNumber;
}
