package com.Turfbooking.models.request;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Valid
public class TimeSlotRequest {

    @NotEmpty
    @Min(value = 1, message = "Slot number should be <= 1.")
    private Integer slotNumber;

    private String turfId;
    private Double price;
    private LocalDate date;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

}
