package com.Turfbooking.models.response;

import com.Turfbooking.documents.BusinessConfig;
import com.Turfbooking.documents.TurfSlotPricing;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class UpdateBusinessConfigResponse {

    private String day;
    private LocalDate date;
    private LocalDateTime openTime;
    private LocalDateTime closeTime;
    private List<TurfSlotPricing> pricings;

    public UpdateBusinessConfigResponse(BusinessConfig config) {
        this.day = config.getDay();
        this.date = config.getDate();
        this.openTime = config.getOpenTime();
        this.closeTime = config.getCloseTime();
        this.pricings = config.getPricing();

    }
}
