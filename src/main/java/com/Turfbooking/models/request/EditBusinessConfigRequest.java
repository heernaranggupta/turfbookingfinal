package com.Turfbooking.models.request;

import com.Turfbooking.documents.TurfSlotPricing;
import lombok.Getter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class EditBusinessConfigRequest {

    @NotNull
    @NotEmpty
    private String businessId; //phone number or username
    @NotNull
    private String day;
    private LocalDate date;
    private LocalDateTime openTime;
    private LocalDateTime closeTime;
    private List<TurfSlotPricing> pricings;

}
