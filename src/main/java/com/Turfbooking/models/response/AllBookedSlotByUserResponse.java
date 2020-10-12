package com.Turfbooking.models.response;

import com.Turfbooking.documents.BookedTimeSlot;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class AllBookedSlotByUserResponse {

    private List<BookedTimeSlot> bookedTimeSlots;

}
