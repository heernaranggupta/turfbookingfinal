package com.Turfbooking.models.request;

import lombok.Getter;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Valid
@Getter
public class GetAllSlotsRequest {

    private String turfId;
    private LocalDate date;
    //change to get these variables - openTime and closeTime from config
    private LocalDateTime openTime;
    private LocalDateTime closeTime;
    private Integer slotDuration;

}
