package com.Turfbooking.models.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Slot {

    private String turfId;
    private Integer slotNumber;
    private Double price;
    private LocalDate date;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
