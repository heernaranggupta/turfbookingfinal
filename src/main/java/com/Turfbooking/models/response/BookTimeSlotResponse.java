package com.Turfbooking.models.response;

import com.Turfbooking.documents.TimeSlot;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Data
public class BookTimeSlotResponse {

    private String userId;
    private String companyId;
    private Integer slotNumber;
    private LocalDate date;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime timestamp;

    public BookTimeSlotResponse(TimeSlot timeSlot){
        this.userId = timeSlot.getUserId();
        this.companyId = timeSlot.getCompanyId();
        this.slotNumber = timeSlot.getSlotNumber();
        this.date = timeSlot.getDate();
        this.startTime = timeSlot.getStartTime();
        this.endTime = timeSlot.getEndTime();
        this.timestamp = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
    }

}
