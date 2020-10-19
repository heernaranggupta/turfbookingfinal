package com.Turfbooking.models.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class UpdateBusinessConfigRequest {

    private String businessId; //phone number or username

    private String day;
    private LocalDate date;
    private LocalDateTime openTime;
    private LocalDateTime closeTime;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Double price;
    private String coupon;
}
