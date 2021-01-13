package com.Turfbooking.service;

import com.Turfbooking.models.request.BusinessViewAllBookingRequest;
import com.Turfbooking.models.request.CancelOrUnavailableSlotRequest;
import com.Turfbooking.models.request.CreateBusinessLoginRequest;
import com.Turfbooking.models.request.CreateBusinessRequest;
import com.Turfbooking.models.request.CreateRescheduleBookingRequest;
import com.Turfbooking.models.request.CreateUpdatePasswordRequest;
import com.Turfbooking.models.request.GetAllSlotsBusinessRequest;
import com.Turfbooking.models.request.UpdateBusinessRequest;
import com.Turfbooking.models.response.BusinessResponse;
import com.Turfbooking.models.response.CreateBusinessLoginResponse;
import com.Turfbooking.models.response.CreateBusinessResponse;
import com.Turfbooking.models.response.CreateBusinessUpdateResponse;
import com.Turfbooking.models.response.CreatePasswordResponse;
import com.Turfbooking.models.response.GetAllSlotsResponse;
import com.Turfbooking.models.response.RescheduleBookingResponse;
import com.Turfbooking.models.response.TimeSlotResponse;

import java.util.List;


public interface BusinessService {

    CreateBusinessResponse createBusinessUser(CreateBusinessRequest createBusinessRequest);

    CreateBusinessLoginResponse businessLogin(CreateBusinessLoginRequest createBusinessLoginRequest);

    CreatePasswordResponse resetPassword(CreateUpdatePasswordRequest createUpdatePasswordRequest);

//    BookTimeSlotResponse bookSlot(BookTimeSlotRequest bookTimeSlotRequest);

    CreateBusinessUpdateResponse updateBusiness(UpdateBusinessRequest updateBusinessRequest);

    GetAllSlotsResponse getAllSlots(GetAllSlotsBusinessRequest getAllSlotsBusinessRequest);


    TimeSlotResponse makeSlotUnavailable(CancelOrUnavailableSlotRequest cancelOrUnavailableSlotRequest);

    RescheduleBookingResponse rescheduleBooking(CreateRescheduleBookingRequest createRescheduleBookingRequest);

    TimeSlotResponse cancelBooking(CancelOrUnavailableSlotRequest cancelOrUnavailableSlotRequest);

    List<TimeSlotResponse> viewAllBooking(BusinessViewAllBookingRequest businessViewAllBookingRequest);

    List<BusinessResponse> getAllBusinessUsers();
}
