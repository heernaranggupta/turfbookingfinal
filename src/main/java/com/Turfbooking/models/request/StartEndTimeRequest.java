package com.Turfbooking.models.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class StartEndTimeRequest {

    private String day;
    private LocalDate date;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Double price;
}
