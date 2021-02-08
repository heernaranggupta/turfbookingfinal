package com.Turfbooking.models.response;

import com.Turfbooking.documents.BookedTimeSlot;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Valid
public class RescheduleBookingResponse {

    @NotEmpty(message = "booking id should be not null")
    private String bookingId;
    private String userId;
    @NotEmpty(message = "turf id should be not null")
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
