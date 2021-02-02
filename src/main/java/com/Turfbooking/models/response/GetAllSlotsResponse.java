package com.Turfbooking.models.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class GetAllSlotsResponse {

    private List<TimeSlotResponse> turf01;
    private List<TimeSlotResponse> turf02;
    private List<TimeSlotResponse> turf03;

}


