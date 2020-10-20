package com.Turfbooking.models.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class BusinessViewAllBookingRequest {

    private String userId;//phoneNumber or unique id
    private String status;
    private LocalDate fromDate;
    private LocalDate toDate;

}
