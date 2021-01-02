package com.Turfbooking.models.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class ConfigByDayRequest {

        private String day;
        private LocalDate date;
        private LocalTime openTime;
        private LocalTime closeTime;
        private List<StartEndTimeDayRequest> startEndTimeDayRequestList;


}
