package com.Turfbooking.service;

import com.Turfbooking.models.request.BusinessViewAllBookingRequest;
import com.Turfbooking.models.request.CancelOrUnavailableSlotRequest;
import com.Turfbooking.models.request.CreateRescheduleBookingRequest;
import com.Turfbooking.models.request.CreateUpdatePasswordRequest;
import com.Turfbooking.models.request.CreateUserRequest;
import com.Turfbooking.models.request.GetAllSlotsBusinessRequest;
import com.Turfbooking.models.request.UpdateBusinessRequest;
import com.Turfbooking.models.request.UserLoginRequest;
import com.Turfbooking.models.response.CommonResponse;
import com.Turfbooking.models.response.CreateBusinessUpdateResponse;
import com.Turfbooking.models.response.CreatePasswordResponse;
import com.Turfbooking.models.response.CreateUserResponse;
import com.Turfbooking.models.response.GetAllSlotsResponse;
import com.Turfbooking.models.response.RescheduleBookingResponse;
import com.Turfbooking.models.response.TimeSlotResponse;
import com.Turfbooking.models.response.UserResponse;

import java.util.List;


public interface BusinessService {

    CreateUserResponse createBusinessUser(CreateUserRequest createUserRequest);

    CreateUserResponse businessLogin(UserLoginRequest userLoginRequest);

    CreatePasswordResponse resetPassword(CreateUpdatePasswordRequest createUpdatePasswordRequest);

    CreateBusinessUpdateResponse updateBusiness(UpdateBusinessRequest updateBusinessRequest);

    GetAllSlotsResponse getAllSlots(GetAllSlotsBusinessRequest getAllSlotsBusinessRequest);

    TimeSlotResponse makeSlotUnavailable(CancelOrUnavailableSlotRequest cancelOrUnavailableSlotRequest);

    RescheduleBookingResponse rescheduleBooking(CreateRescheduleBookingRequest createRescheduleBookingRequest);

    TimeSlotResponse cancelBooking(CancelOrUnavailableSlotRequest cancelOrUnavailableSlotRequest);

    List<TimeSlotResponse> viewAllBooking(BusinessViewAllBookingRequest businessViewAllBookingRequest);

    List<UserResponse> getAllBusinessUsers();

    CommonResponse paymentAccepted(String bookingId);

    CommonResponse getAllFutureBookings();

    CommonResponse cancelBookingByAdmin(String bookingId);

    CommonResponse getUserDetailsByContactNumber(String mobileNo);
}
