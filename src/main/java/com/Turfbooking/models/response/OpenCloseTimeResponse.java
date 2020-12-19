package com.Turfbooking.models.response;

import com.Turfbooking.documents.OpenCloseTime;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class OpenCloseTimeResponse {

    private String _id;
    private String day;
    private LocalDate date;
    private LocalDateTime openTime;
    private LocalDateTime closeTime;
    private LocalDateTime timestamp;

    public OpenCloseTimeResponse(OpenCloseTime openCloseTime) {
        this._id = openCloseTime.get_id();
        this.day = openCloseTime.getDay();
        this.date = openCloseTime.getDate();
        this.openTime = openCloseTime.getOpenTime();
        this.closeTime = openCloseTime.getCloseTime();
        this.timestamp = openCloseTime.getTimestamp();
    }
}
