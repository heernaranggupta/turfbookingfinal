package com.Turfbooking.models.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@Valid
@Getter
@Setter
public class GetAllSlotsRequest {

    private List<String> turfIds;
    private LocalDate date;
    private Integer slotDuration;

}

