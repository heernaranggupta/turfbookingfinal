package com.Turfbooking.documents;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Document
public class SlotsInBookingTemp {

    @Id
    private String id;
    private String turfId;
    private Double price;
    private LocalDate date;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime timestamp;

    public SlotsInBookingTemp(String turfId, Double price, LocalDate date, LocalDateTime startTime, LocalDateTime endTime, LocalDateTime timestamp) {
        this.turfId = turfId;
        this.price = price;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.timestamp = timestamp;
    }
}
