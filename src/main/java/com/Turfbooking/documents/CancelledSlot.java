package com.Turfbooking.documents;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@Document
@NoArgsConstructor
@AllArgsConstructor
public class CancelledSlot {

    @Id
    private String _id;
    private String bookingId;
    private String userId;
    private String turfId;
    private Double price;
    private String status;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String orderId;
    private String refundId;
    private LocalDateTime timeStamp;

    public CancelledSlot(BookedTimeSlot bookedTimeSlot) {
        this.bookingId = bookedTimeSlot.getBookingId();
        this.userId = bookedTimeSlot.getUserId();
        this.turfId = bookedTimeSlot.getTurfId();
        this.price = bookedTimeSlot.getPrice();
        this.date = bookedTimeSlot.getDate();
        this.startTime = bookedTimeSlot.getStartTime().toLocalTime();
        this.endTime = bookedTimeSlot.getEndTime().toLocalTime();
        this.orderId = bookedTimeSlot.getOrderId();
        this.timeStamp = bookedTimeSlot.getTimeStamp();
    }
}
