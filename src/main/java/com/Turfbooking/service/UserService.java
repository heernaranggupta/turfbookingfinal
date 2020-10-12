package com.Turfbooking.service;

import com.Turfbooking.models.request.CreateProfileUpdateRequest;
import com.Turfbooking.models.request.CreateUserRequest;
import com.Turfbooking.models.request.UserLoginRequest;
import com.Turfbooking.models.request.ValidateOtpRequest;
import com.Turfbooking.models.response.CreateProfileUpdateResponse;
import com.Turfbooking.models.response.CreateUserLoginResponse;
import com.Turfbooking.models.response.CreateUserResponse;
import com.Turfbooking.models.response.ValidateOtpResponse;

import javax.validation.Valid;

public interface UserService {

    CreateUserResponse createNewUser(CreateUserRequest createUserRequest);

    CreateUserLoginResponse userLogin(UserLoginRequest userLoginRequest);

    ValidateOtpResponse validateOTP(ValidateOtpRequest request);

    CreateProfileUpdateResponse updateProfile(CreateProfileUpdateRequest createUpdateProfileRequest);
}
