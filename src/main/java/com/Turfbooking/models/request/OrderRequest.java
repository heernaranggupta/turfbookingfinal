package com.Turfbooking.models.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Valid
@Getter
@Setter
public class OrderRequest {

    private String userId;
    @NotEmpty
    private String transactionId;
    private List<TimeSlotRequest> timeSlots;

}
