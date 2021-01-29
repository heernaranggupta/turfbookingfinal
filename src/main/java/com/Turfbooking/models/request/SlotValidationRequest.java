package com.Turfbooking.models.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SlotValidationRequest {

    private List<TimeSlotRequest> timeSlotRequestList;
}
