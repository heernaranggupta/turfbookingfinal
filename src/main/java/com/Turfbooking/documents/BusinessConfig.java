package com.Turfbooking.documents;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@Document
public class BusinessConfig {

    private String _id;
    private String day;
    private LocalDate date;
    private LocalDateTime openTime;
    private LocalDateTime closeTime;
    private TurfSlotPricing pricing;

}
