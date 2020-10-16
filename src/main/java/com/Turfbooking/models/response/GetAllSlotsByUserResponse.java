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

    List<BookTimeSlotResponse> responseList;

    public GetAllSlotsByUserResponse(List<BookTimeSlotResponse> responseList) {
        this.responseList = responseList;
    }
}


