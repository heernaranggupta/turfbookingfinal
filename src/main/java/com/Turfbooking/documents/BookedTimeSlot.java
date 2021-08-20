package com.Turfbooking.documents;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Document
public class BookedTimeSlot {

    @Id
    private String _id;
    private String bookingId;
    private String userId;
    private String turfId;
    private Double payedAmount;
    private Double remainingAmount;
    private Boolean remainingAmountPayed;
    private String status;
    private LocalDate date;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String orderId;
    private LocalDateTime timeStamp;

}
