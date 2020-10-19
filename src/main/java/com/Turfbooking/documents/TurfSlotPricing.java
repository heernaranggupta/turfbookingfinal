package com.Turfbooking.documents;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Builder
@Document
public class TurfSlotPricing {

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Double price;
    private String coupon;

}
