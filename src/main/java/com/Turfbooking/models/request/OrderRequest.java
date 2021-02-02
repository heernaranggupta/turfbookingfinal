package com.Turfbooking.models.request;

import lombok.Getter;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter
@Valid
public class OrderRequest {

    @NotEmpty
    private String userId;
    @NotEmpty
    private String transactionId;
    private List<TimeSlotRequest> timeSlots;

}
