package com.Turfbooking.models.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SlotValidationResponse {

    List<TimeSlotResponse> timeSlotResponses;
    Boolean isDeleted;
    Integer deleteCount;

}
