package com.Turfbooking.models.response;

import com.Turfbooking.documents.BookedTimeSlot;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class RescheduleBookingResponse {

    private String bookingId;
    private String userId;
    private String turfId;
    private String status;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDateTime timestamp;

    public RescheduleBookingResponse(BookedTimeSlot bookedTimeSlot) {
        this.bookingId = bookedTimeSlot.getBookingId();
        this.userId = bookedTimeSlot.getUserId();
        this.turfId = bookedTimeSlot.getTurfId();
        this.status = bookedTimeSlot.getStatus();
        this.date = bookedTimeSlot.getDate();
        this.startTime = bookedTimeSlot.getStartTime().toLocalTime();
        this.endTime = bookedTimeSlot.getEndTime().toLocalTime();
        this.timestamp = bookedTimeSlot.getTimeStamp(); //LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
    }
}
