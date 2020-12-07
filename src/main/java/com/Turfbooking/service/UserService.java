package com.Turfbooking.service;

import com.Turfbooking.models.request.*;
import com.Turfbooking.models.response.*;

public interface UserService {

    CreateUserResponse createNewUser(CreateUserRequest createUserRequest);

    CreateUserLoginResponse userLogin(UserLoginRequest userLoginRequest);

    AllBookedSlotByUserResponse getAllBookedSlots(String userId);

    CustomerProfileUpdateResponse updateProfile(CustomerProfileUpdateRequest customerProfileUpdateRequest);

    BookTimeSlotResponse cancelBookedSlot(CancelOrUnavailableSlotRequest cancelOrUnavailableSlotRequest);

    BookTimeSlotResponse updateBookedSlot(UpdateBookedTimeSlotRequest updateBookedTimeSlotRequest);

    GetAllSlotsResponse getAllSlotsByDate(GetAllSlotsRequest getAllSlotsRequest);

    CartResponse addToCart(CartRequest cartRequest);

    CartResponse getCart(String PhoneNumber);

    CartResponse removeFromCart(RemoveCartRequest removeCartRequest);

}
