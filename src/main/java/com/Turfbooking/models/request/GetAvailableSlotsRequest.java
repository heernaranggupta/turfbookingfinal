package com.Turfbooking.models.request;

import lombok.Builder;
import lombok.Data;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@Valid
@Builder
@Data
public class GetAvailableSlotsRequest {
    private List<String> turfIds;
    private LocalDate date;

}
