package com.Turfbooking.service;

import com.Turfbooking.models.request.CancelOrUnavailableSlotRequest;
import com.Turfbooking.models.request.CartRequest;
import com.Turfbooking.models.request.CreateUserRequest;
import com.Turfbooking.models.request.CustomerProfileUpdateRequest;
import com.Turfbooking.models.request.GetAllSlotsRequest;
import com.Turfbooking.models.request.RemoveCartRequest;
import com.Turfbooking.models.request.UpdateBookedTimeSlotRequest;
import com.Turfbooking.models.request.UserLoginRequest;
import com.Turfbooking.models.response.AllBookedSlotByUserResponse;
import com.Turfbooking.models.response.CartResponse;
import com.Turfbooking.models.response.CommonResponse;
import com.Turfbooking.models.response.CreateUserLoginResponse;
import com.Turfbooking.models.response.CreateUserResponse;
import com.Turfbooking.models.response.CustomerProfileUpdateResponse;
import com.Turfbooking.models.response.GetAllSlotsResponse;
import com.Turfbooking.models.response.GetAllSlotsResponseForPhoneUser;
import com.Turfbooking.models.response.TimeSlotResponse;
import com.Turfbooking.models.response.UserResponse;
import com.Turfbooking.razorpay.RazorpayException;

public interface UserService {

    CreateUserResponse createNewUser(CreateUserRequest createUserRequest);

    CreateUserLoginResponse userLogin(UserLoginRequest userLoginRequest);

    UserResponse getUser(String userPhoneNumber);

    AllBookedSlotByUserResponse getAllBookedSlots(String userId);

    CustomerProfileUpdateResponse updateProfile(CustomerProfileUpdateRequest customerProfileUpdateRequest);

    TimeSlotResponse cancelBookedSlot(CancelOrUnavailableSlotRequest cancelOrUnavailableSlotRequest, String userID) throws RazorpayException;

    TimeSlotResponse updateBookedSlot(UpdateBookedTimeSlotRequest updateBookedTimeSlotRequest);

    GetAllSlotsResponse getAllSlotsByDate(GetAllSlotsRequest getAllSlotsRequest);

    CartResponse addToCart(CartRequest cartRequest, Boolean guest);

    CommonResponse getCart(String PhoneNumber, String cartId);

    CartResponse removeFromCart(RemoveCartRequest removeCartRequest);

    GetAllSlotsResponseForPhoneUser getAllSlotsByDatePhoneUser(GetAllSlotsRequest getAllSlotsRequest);

    GetAllSlotsResponseForPhoneUser getAllSlotsByDateCommon(GetAllSlotsRequest getAllSlotsRequest);

    CommonResponse getCartForPhoneUsers(String userID);

}
