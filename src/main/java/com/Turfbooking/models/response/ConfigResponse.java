package com.Turfbooking.models.response;

import com.Turfbooking.documents.OpenCloseTime;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class ConfigResponse {

    private String day;
    private LocalDate date;
    private LocalTime openTime;
    private LocalTime closeTime;
    private Integer slotDuration;
    private List<StartEndTimeResponse> startEndTimeResponseList;
    private String message;

    public ConfigResponse(OpenCloseTime openCloseTime, List<StartEndTimeResponse> startEndTimeResponseList, String message) {
        this.day = openCloseTime.getDay();
        this.date = openCloseTime.getDate();
        this.openTime = openCloseTime.getOpenTime();
        this.closeTime = openCloseTime.getCloseTime();
        this.slotDuration = openCloseTime.getSlotDuration();
        this.startEndTimeResponseList = startEndTimeResponseList;
        this.message = message;
    }

    public ConfigResponse() {
    }

}
