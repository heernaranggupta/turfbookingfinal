package com.Turfbooking.models.response;

import com.Turfbooking.documents.StartEndTime;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class StartEndTimeResponse {

    private String _id;
    private String day;
    private LocalDate date;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Double price;
    private LocalDateTime timestamp;

    public StartEndTimeResponse(StartEndTime startEndTime) {
        this._id = startEndTime.get_id();
        this.day = startEndTime.getDay();
        this.date = startEndTime.getDate();
        this.startTime = startEndTime.getStartTime();
        this.endTime = startEndTime.getEndTime();
        this.price = startEndTime.getPrice();
        this.timestamp = startEndTime.getTimestamp();
    }
}
