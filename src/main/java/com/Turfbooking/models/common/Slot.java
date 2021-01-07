package com.Turfbooking.models.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Slot {

    private String turfId;
    private Double price;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

}
