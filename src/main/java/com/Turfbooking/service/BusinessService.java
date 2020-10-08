package com.Turfbooking.service;

import com.Turfbooking.models.request.CreateBusinessLoginRequest;
import com.Turfbooking.models.request.CreateUpdatePasswordRequest;
import com.Turfbooking.models.response.CreateBusinessLoginResponse;
import com.Turfbooking.models.response.CreatePasswordResponse;
import com.Turfbooking.models.request.BookTimeSlotRequest;
import com.Turfbooking.models.response.BookTimeSlotResponse;


public interface BusinessService {

    CreateBusinessLoginResponse businessLogin(CreateBusinessLoginRequest createBusinessLoginRequest);

    CreatePasswordResponse resetPassword(CreateUpdatePasswordRequest createUpdatePasswordRequest);



    BookTimeSlotResponse bookSlot(BookTimeSlotRequest bookTimeSlotRequest);

}
