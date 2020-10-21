package com.Turfbooking.models.request;

import lombok.Getter;

import javax.validation.Valid;
import java.time.LocalDate;

@Valid
@Getter
public class GetBusinessConfigRequest {

    private String day;
    private LocalDate date;

}
