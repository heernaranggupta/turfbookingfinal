package com.Turfbooking.models.response;

import com.Turfbooking.documents.BookedTimeSlot;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class BookTimeSlotResponse {

    private String bookingId;
    private String userId;
    private String turfId;
    private Integer slotNumber;
    private String status;
    private LocalDate date;
    private Double price;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime timestamp;

    public BookTimeSlotResponse(BookedTimeSlot bookedTimeSlot){
        this.bookingId = bookedTimeSlot.getBookingId();
        this.userId = bookedTimeSlot.getUserId();
        this.turfId = bookedTimeSlot.getTurfId();
        this.slotNumber = bookedTimeSlot.getSlotNumber();
        this.status = bookedTimeSlot.getStatus();
        this.date = bookedTimeSlot.getDate().toLocalDate();
        this.price = bookedTimeSlot.getPrice();
        this.startTime = bookedTimeSlot.getStartTime();
        this.endTime = bookedTimeSlot.getEndTime();
        this.timestamp = bookedTimeSlot.getTimeStamp(); //LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
    }

    public BookTimeSlotResponse(String userId, String turfId, Integer slotNumber, String status, LocalDate date, Double price, LocalDateTime startTime, LocalDateTime endTime, LocalDateTime timestamp) {
        this.userId = userId;
        this.turfId = turfId;
        this.slotNumber = slotNumber;
        this.status = status;
        this.date = date;
        this.price = price;
        this.startTime = startTime;
        this.endTime = endTime;
        this.timestamp = timestamp;
    }

    public BookTimeSlotResponse(String turfId, Integer slotNumber, String status, LocalDate date, Double price, LocalDateTime startTime, LocalDateTime endTime) {
        this.turfId = turfId;
        this.slotNumber = slotNumber;
        this.status = status;
        this.date = date;
        this.price = price;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
