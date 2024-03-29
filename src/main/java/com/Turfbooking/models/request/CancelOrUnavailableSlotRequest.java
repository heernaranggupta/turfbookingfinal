package com.Turfbooking.models.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

@Valid
@Getter
@Setter
public class CancelOrUnavailableSlotRequest {

    @NotNull
    private String turfId;
    @NotNull
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

}
