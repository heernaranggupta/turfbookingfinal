package com.Turfbooking.models.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class OpenCloseTimeRequest {

    private String day;
    private LocalDate date;
    @NotEmpty
    private LocalDateTime openTime;
    @NotEmpty
    private LocalDateTime closeTime;
}
