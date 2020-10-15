package com.Turfbooking.models.request;

import lombok.Getter;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Valid
@Getter
public class GetAllSlotsBusinessRequest {

    private List<String> turfIds;
    private LocalDate date;
    //change to get these variables - openTime and closeTime from config
    private LocalDateTime openTime;
    private LocalDateTime closeTime;
    private Integer slotDuration;

}