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
public class GetAllSlotsByUserResponse {

    List<TimeSlotResponse> responseList;

    public GetAllSlotsByUserResponse(List<TimeSlotResponse> responseList) {
        this.responseList = responseList;
    }
}


