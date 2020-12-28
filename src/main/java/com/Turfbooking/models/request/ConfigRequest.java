package com.Turfbooking.models.request;

import com.Turfbooking.models.common.StartEndTimeRequest;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ConfigRequest {

    private String day;
    private LocalDate date;
    private LocalDateTime openTime;
    private LocalDateTime closeTime;
    private List<StartEndTimeRequest> startEndTimeRequestList;

}
