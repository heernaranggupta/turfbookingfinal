package com.Turfbooking.service;

import com.Turfbooking.models.request.BookTimeSlotRequest;
import com.Turfbooking.models.request.CreateBusinessLoginRequest;
import com.Turfbooking.models.request.CreateUpdatePasswordRequest;
import com.Turfbooking.models.request.GetAllSlotsBusinessRequest;
import com.Turfbooking.models.request.UpdateBusinessRequest;
import com.Turfbooking.models.response.BookTimeSlotResponse;
import com.Turfbooking.models.response.CreateBusinessLoginResponse;
import com.Turfbooking.models.response.CreateBusinessUpdateResponse;
import com.Turfbooking.models.response.CreatePasswordResponse;
import com.Turfbooking.models.response.GetAllSlotsResponse;


public interface BusinessService {

    CreateBusinessLoginResponse businessLogin(CreateBusinessLoginRequest createBusinessLoginRequest);

    CreatePasswordResponse resetPassword(CreateUpdatePasswordRequest createUpdatePasswordRequest);

    BookTimeSlotResponse bookSlot(BookTimeSlotRequest bookTimeSlotRequest);

    CreateBusinessUpdateResponse updateBusiness(UpdateBusinessRequest updateBusinessRequest);

    GetAllSlotsResponse getAllSlots(GetAllSlotsBusinessRequest getAllSlotsBusinessRequest);

}
