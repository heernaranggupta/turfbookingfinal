package com.Turfbooking.service;

import com.Turfbooking.models.request.BookTimeSlotRequest;
import com.Turfbooking.models.response.BookTimeSlotResponse;

public interface BusinessService {

    BookTimeSlotResponse bookSlot(BookTimeSlotRequest bookTimeSlotRequest);

}
