package com.Turfbooking.models.request;

import lombok.Getter;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Valid
@Getter
public class GetAllSlotsRequest {

    private String companyId;
    private LocalDate date;
    private LocalDateTime openTime;
    private LocalDateTime closeTime;
    private Integer slotDuration;

}
