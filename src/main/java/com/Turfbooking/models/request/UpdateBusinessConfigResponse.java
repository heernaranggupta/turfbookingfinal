package com.Turfbooking.models.request;

import com.Turfbooking.documents.BusinessConfig;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class UpdateBusinessConfigResponse {

    private String day;
    private LocalDate date;
    private LocalDateTime openTime;
    private LocalDateTime closeTime;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Double price;
    private String coupon;

    public UpdateBusinessConfigResponse(BusinessConfig config) {
        this.day = config.getDay();
        this.date = config.getDate();
        this.openTime = config.getOpenTime();
        this.closeTime = config.getCloseTime();
        this.startTime = config.getPricing().getStartTime();
        this.endTime = config.getPricing().getEndTime();
        this.price = config.getPricing().getPrice();
        this.coupon = config.getPricing().getCoupon();
    }
}
