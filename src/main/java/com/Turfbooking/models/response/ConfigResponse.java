package com.Turfbooking.models.response;

import com.Turfbooking.documents.OpenCloseTime;
import com.Turfbooking.documents.StartEndTime;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ConfigResponse {

    private String day;
    private LocalDate date;
    private LocalDateTime openTime;
    private LocalDateTime closeTime;
    private List<StartEndTime> startEndTimeList;
    private String message;

    public ConfigResponse(OpenCloseTime openCloseTime, List<StartEndTime> startEndTimeList, String message) {
        this.day = openCloseTime.getDay();
        this.date = openCloseTime.getDate();
        this.openTime = openCloseTime.getOpenTime();
        this.closeTime = openCloseTime.getCloseTime();
        this.startEndTimeList = startEndTimeList;
        this.message = message;
    }
}
