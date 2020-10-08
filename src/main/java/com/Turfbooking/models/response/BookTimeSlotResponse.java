package com.Turfbooking.models.response;

import com.Turfbooking.documents.BookedTimeSlot;
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

    public BookTimeSlotResponse(BookedTimeSlot bookedTimeSlot){
        this.userId = bookedTimeSlot.getUserId();
        this.companyId = bookedTimeSlot.getCompanyId();
        this.slotNumber = bookedTimeSlot.getSlotNumber();
        this.date = bookedTimeSlot.getDate();
        this.startTime = bookedTimeSlot.getStartTime();
        this.endTime = bookedTimeSlot.getEndTime();
        this.timestamp = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
    }

}
