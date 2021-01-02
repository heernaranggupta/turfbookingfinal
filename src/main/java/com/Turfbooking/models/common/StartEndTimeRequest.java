package com.Turfbooking.models.common;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class StartEndTimeRequest {

    private String turfId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Double price;
}
