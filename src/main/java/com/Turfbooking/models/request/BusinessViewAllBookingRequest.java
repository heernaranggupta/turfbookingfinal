package com.Turfbooking.models.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class BusinessViewAllBookingRequest {

    private String userId;//phoneNumber or unique id
    private String status;
    private LocalDate fromDate;
    private LocalDate toDate;

}
