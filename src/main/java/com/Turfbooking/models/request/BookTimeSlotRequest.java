package com.Turfbooking.models.request;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Valid
public class BookTimeSlotRequest {

    @NotEmpty
    private Integer slotNumber;
    private String turfId;
    @NotEmpty
    private String userId; // to know who booked this slot
    //not in past
    private LocalDate date;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
//    private Integer totalNumberOfSlots;

}
