package com.Turfbooking.models.request;

import lombok.Getter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
public class OrderRequest {

    @NotNull
    @NotEmpty
    private String userId;
    private List<TimeSlotRequest> timeSlots;

}
