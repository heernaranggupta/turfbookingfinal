package com.Turfbooking.service;

import com.Turfbooking.models.request.BookTimeSlotRequest;
import com.Turfbooking.models.request.GetAllSlotsRequest;
import com.Turfbooking.models.response.BookTimeSlotResponse;
import com.Turfbooking.models.response.GetAllSlotsResponse;

public interface BusinessService {

    BookTimeSlotResponse bookSlot(BookTimeSlotRequest bookTimeSlotRequest);

    GetAllSlotsResponse getAllSlots(GetAllSlotsRequest getAllSlotsRequest);

}
