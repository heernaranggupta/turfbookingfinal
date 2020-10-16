package com.Turfbooking.service;

import com.Turfbooking.models.request.BookTimeSlotRequest;
import com.Turfbooking.models.request.CreateUserRequest;
import com.Turfbooking.models.request.CustomerProfileUpdateRequest;
import com.Turfbooking.models.request.GetAllSlotsRequest;
import com.Turfbooking.models.request.UpdateBookedTimeSlotRequest;
import com.Turfbooking.models.request.UserLoginRequest;
import com.Turfbooking.models.response.AllBookedSlotByUserResponse;
import com.Turfbooking.models.response.BookTimeSlotResponse;
import com.Turfbooking.models.response.CreateUserLoginResponse;
import com.Turfbooking.models.response.CreateUserResponse;
import com.Turfbooking.models.response.CustomerProfileUpdateResponse;
import com.Turfbooking.models.response.GetAllSlotsByUserResponse;

public interface UserService {

    CreateUserResponse createNewUser(CreateUserRequest createUserRequest);

    CreateUserLoginResponse userLogin(UserLoginRequest userLoginRequest);

    AllBookedSlotByUserResponse getAllBookedSlots(String userId);

    CustomerProfileUpdateResponse updateProfile(CustomerProfileUpdateRequest customerProfileUpdateRequest);

    BookTimeSlotResponse cancelBookedSlot(String bookingId);

    BookTimeSlotResponse updateBookedSlot(UpdateBookedTimeSlotRequest updateBookedTimeSlotRequest);

    BookTimeSlotResponse bookSlot(BookTimeSlotRequest bookTimeSlotRequest);

    GetAllSlotsByUserResponse getAllSlotsByDate(GetAllSlotsRequest getAllSlotsRequest);
}
