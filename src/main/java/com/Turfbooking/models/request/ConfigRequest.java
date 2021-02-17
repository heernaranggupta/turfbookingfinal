package com.Turfbooking.models.request;

import com.Turfbooking.models.common.StartEndTimeRequest;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class ConfigRequest {

    private String day;
    private LocalDate date;
    private LocalTime openTime;
    private LocalTime closeTime;
    private Integer slotDuration;
    private List<StartEndTimeRequest> startEndTimeRequestList;

}
