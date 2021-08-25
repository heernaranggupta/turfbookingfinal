package com.Turfbooking.models.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import java.util.List;

@Valid
@Getter
@Setter
public class OrderRequest {

    private String userId;
    private String transactionId;
    private List<TimeSlotRequest> timeSlots;

}
