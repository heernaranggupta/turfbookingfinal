package com.Turfbooking.models.response;

import com.Turfbooking.documents.StartEndTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
public class StartEndTimeResponse {

    private String day;
    private LocalDate date;
    private String turfId;
    private LocalTime startTime;
    private LocalTime endTime;
    private Double price;
    private LocalDateTime timestamp;

    public StartEndTimeResponse(StartEndTime startEndTime) {

        this.day = startEndTime.getDay();
        this.date = startEndTime.getDate();
        this.turfId = startEndTime.getTurfId();
        this.startTime = startEndTime.getStartTime();
        this.endTime = startEndTime.getEndTime();
        this.price = startEndTime.getPrice();
        this.timestamp = startEndTime.getTimestamp();
    }


}
