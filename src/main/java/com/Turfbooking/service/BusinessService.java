package com.Turfbooking.service;

import com.Turfbooking.models.request.BookTimeSlotRequest;
import com.Turfbooking.models.request.BusinessViewAllBookingRequest;
import com.Turfbooking.models.request.CancelOrUnavailableSlotRequest;
import com.Turfbooking.models.request.CreateBusinessLoginRequest;
import com.Turfbooking.models.request.CreateRescheduleBookingRequest;
import com.Turfbooking.models.request.CreateUpdatePasswordRequest;
import com.Turfbooking.models.request.EditBusinessConfigRequest;
import com.Turfbooking.models.request.GetAllSlotsBusinessRequest;
import com.Turfbooking.models.response.UpdateBusinessConfigResponse;
import com.Turfbooking.models.request.UpdateBusinessRequest;
import com.Turfbooking.models.response.BookTimeSlotResponse;
import com.Turfbooking.models.response.CreateBusinessLoginResponse;
import com.Turfbooking.models.response.CreateBusinessUpdateResponse;
import com.Turfbooking.models.response.CreatePasswordResponse;
import com.Turfbooking.models.response.GetAllSlotsResponse;
import com.Turfbooking.models.response.RescheduleBookingResponse;
import com.Turfbooking.models.request.UpdateBusinessConfigRequest;

import java.util.List;


public interface BusinessService {

    CreateBusinessLoginResponse businessLogin(CreateBusinessLoginRequest createBusinessLoginRequest);

    CreatePasswordResponse resetPassword(CreateUpdatePasswordRequest createUpdatePasswordRequest);

    BookTimeSlotResponse bookSlot(BookTimeSlotRequest bookTimeSlotRequest);

    CreateBusinessUpdateResponse updateBusiness(UpdateBusinessRequest updateBusinessRequest);

    GetAllSlotsResponse getAllSlots(GetAllSlotsBusinessRequest getAllSlotsBusinessRequest);

    BookTimeSlotResponse makeSlotUnavailable(CancelOrUnavailableSlotRequest cancelOrUnavailableSlotRequest);

    RescheduleBookingResponse rescheduleBooking(CreateRescheduleBookingRequest createRescheduleBookingRequest);

    BookTimeSlotResponse cancelBooking(CancelOrUnavailableSlotRequest cancelOrUnavailableSlotRequest);

    List<BookTimeSlotResponse> viewAllBooking(BusinessViewAllBookingRequest businessViewAllBookingRequest);

    UpdateBusinessConfigResponse updateBusinessConfig(UpdateBusinessConfigRequest updateBusinessConfigRequest);

    UpdateBusinessConfigResponse editBusinessConfig(EditBusinessConfigRequest editBusinessConfigRequest);

}
