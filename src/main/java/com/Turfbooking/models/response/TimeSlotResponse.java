package com.Turfbooking.models.response;

import com.Turfbooking.documents.BookedTimeSlot;
import com.Turfbooking.documents.CancelledSlot;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class TimeSlotResponse {

    private String bookingId;
    private String userId;
    private String turfId;
    private Double price;
    private String status;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDateTime timestamp;

    public TimeSlotResponse(BookedTimeSlot bookedTimeSlot) {
        this.bookingId = bookedTimeSlot.getBookingId();
        this.userId = bookedTimeSlot.getUserId();
        this.turfId = bookedTimeSlot.getTurfId();
        this.status = bookedTimeSlot.getStatus();
        this.date = bookedTimeSlot.getDate();
        this.startTime = bookedTimeSlot.getStartTime();
        this.endTime = bookedTimeSlot.getEndTime();
        this.timestamp = bookedTimeSlot.getTimeStamp();
    }

    public TimeSlotResponse(CancelledSlot cancelledSlot) {
        this.bookingId = cancelledSlot.getBookingId();
        this.userId = cancelledSlot.getUserId();
        this.turfId = cancelledSlot.getTurfId();
        this.status = cancelledSlot.getStatus();
        this.date = cancelledSlot.getDate();
        this.startTime = cancelledSlot.getStartTime();
        this.endTime = cancelledSlot.getEndTime();
        this.timestamp = cancelledSlot.getTimeStamp();
    }

    public TimeSlotResponse(String userId, String turfId, String status, LocalDate date, LocalTime startTime, LocalTime endTime, LocalDateTime timestamp) {
        this.userId = userId;
        this.turfId = turfId;
        this.status = status;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.timestamp = timestamp;
    }

    public TimeSlotResponse(String turfId, Double price, String status, LocalDate date, LocalTime startTime, LocalTime endTime) {
        this.turfId = turfId;
        this.price = price;
        this.status = status;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
