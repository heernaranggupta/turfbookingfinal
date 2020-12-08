package com.Turfbooking.models.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class GetAllSlotsResponse {

    List<List<BookTimeSlotResponse>> responseList;

    public GetAllSlotsResponse(List<List<BookTimeSlotResponse>> responseList) {
        this.responseList = responseList;
    }
}


