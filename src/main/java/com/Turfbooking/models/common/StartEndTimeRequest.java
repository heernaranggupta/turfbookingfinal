package com.Turfbooking.models.common;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
public class StartEndTimeRequest {

    private String turfId;
    private LocalTime startTime;
    private LocalTime endTime;
    private Double price;
}
