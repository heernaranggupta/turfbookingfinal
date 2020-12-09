package com.Turfbooking.models.response;

import com.Turfbooking.documents.BookedTimeSlot;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TimeSlotResponse {

    private String bookingId;
    private String userId;
    private String turfId;
    private Integer slotNumber;
    private Double price;
    private String status;
    private LocalDate date;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime timestamp;

    public TimeSlotResponse(BookedTimeSlot bookedTimeSlot) {
        this.bookingId = bookedTimeSlot.getBookingId();
        this.userId = bookedTimeSlot.getUserId();
        this.turfId = bookedTimeSlot.getTurfId();
        this.slotNumber = bookedTimeSlot.getSlotNumber();
        this.status = bookedTimeSlot.getStatus();
        this.date = bookedTimeSlot.getDate().toLocalDate();
        this.startTime = bookedTimeSlot.getStartTime();
        this.endTime = bookedTimeSlot.getEndTime();
        this.timestamp = bookedTimeSlot.getTimeStamp(); //LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
    }

    public TimeSlotResponse(String userId, String turfId, Integer slotNumber, String status, LocalDate date, LocalDateTime startTime, LocalDateTime endTime, LocalDateTime timestamp) {
        this.userId = userId;
        this.turfId = turfId;
        this.slotNumber = slotNumber;
        this.status = status;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.timestamp = timestamp;
    }

    public TimeSlotResponse(String turfId, Integer slotNumber, Double price, String status, LocalDate date, LocalDateTime startTime, LocalDateTime endTime) {
        this.turfId = turfId;
        this.slotNumber = slotNumber;
        this.price = price;
        this.status = status;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
