package com.Turfbooking.models.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class AllBookedSlotByUserResponse {

    private List<TimeSlotResponse> bookedTimeSlots;

}
