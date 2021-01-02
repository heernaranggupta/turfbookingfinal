package com.Turfbooking.models.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
public class StartEndTimeDayRequest {

    private String turfId;
    private LocalTime startTime;
    private LocalTime endTime;
    private Double price;

}
