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
    private String status;
    private LocalDate date;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime timestamp;

    public BookTimeSlotResponse(BookedTimeSlot bookedTimeSlot){
        this.userId = bookedTimeSlot.getUserId();
        this.companyId = bookedTimeSlot.getCompanyId();
        this.slotNumber = bookedTimeSlot.getSlotNumber();
        this.status = bookedTimeSlot.getStatus();
        this.date = bookedTimeSlot.getDate();
        this.startTime = bookedTimeSlot.getStartTime();
        this.endTime = bookedTimeSlot.getEndTime();
        this.timestamp = bookedTimeSlot.getTimeStamp(); //LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
    }

    public BookTimeSlotResponse(String userId, String companyId, Integer slotNumber, String status, LocalDate date, LocalDateTime startTime, LocalDateTime endTime, LocalDateTime timestamp) {
        this.userId = userId;
        this.companyId = companyId;
        this.slotNumber = slotNumber;
        this.status = status;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.timestamp = timestamp;
    }

    public BookTimeSlotResponse(String companyId, Integer slotNumber, String status, LocalDate date, LocalDateTime startTime, LocalDateTime endTime) {
        this.companyId = companyId;
        this.slotNumber = slotNumber;
        this.status = status;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
