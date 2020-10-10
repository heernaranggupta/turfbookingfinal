package com.Turfbooking.models.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class GetAllSlotsResponse {

    List<BookTimeSlotResponse> responseList;

    public GetAllSlotsResponse(List<BookTimeSlotResponse> responseList) {
        this.responseList = responseList;
    }
}


