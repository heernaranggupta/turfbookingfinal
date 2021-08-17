package com.Turfbooking.models.request;

import com.Turfbooking.models.common.Slot;
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

    public TimeSlotRequest() {
    }

    public TimeSlotRequest(Slot slot) {
        this.turfId = slot.getTurfId();
        this.price = slot.getPrice();
        this.date = slot.getDate();
        this.startTime = slot.getStartTime();
        this.endTime = slot.getEndTime();
    }
}
