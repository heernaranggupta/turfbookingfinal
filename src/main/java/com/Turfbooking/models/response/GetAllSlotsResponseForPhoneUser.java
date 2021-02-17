package com.Turfbooking.models.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class GetAllSlotsResponseForPhoneUser {

    private Map<String, List<TimeSlotResponse>> slotList;

}


