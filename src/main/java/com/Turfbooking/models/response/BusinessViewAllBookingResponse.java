package com.Turfbooking.models.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class BusinessViewAllBookingResponse {

    private List<BookTimeSlotResponse> responsesList;

}
