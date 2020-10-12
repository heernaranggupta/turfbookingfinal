package com.Turfbooking.controller;

import com.Turfbooking.models.request.CreateProfileUpdateRequest;
import com.Turfbooking.models.request.CreateUserRequest;
import com.Turfbooking.models.request.UserLoginRequest;
import com.Turfbooking.models.request.ValidateOtpRequest;
import com.Turfbooking.models.response.CommonResponse;
import com.Turfbooking.models.response.CreateBusinessUpdateResponse;
import com.Turfbooking.models.response.CreateProfileUpdateResponse;
import com.Turfbooking.models.response.CreateUserResponse;
import com.Turfbooking.models.response.UserResponse;
import com.Turfbooking.models.response.CreateUserLoginResponse;
import com.Turfbooking.models.response.ValidateOtpResponse;
import com.Turfbooking.service.UserService;
import com.Turfbooking.utils.ResponseUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequestMapping("/user")
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public CommonResponse<CreateUserResponse> createUser(@RequestBody @Valid CreateUserRequest createUserRequest) {
        CreateUserResponse userResponse = userService.createNewUser(createUserRequest);
        CommonResponse response = new CommonResponse<>(userResponse);
        return ResponseUtilities.createSuccessResponse(response);
    }

    @PostMapping("/login")
    public CommonResponse<UserResponse> userLogin(@Valid @RequestBody UserLoginRequest userLoginRequest) {
        CreateUserLoginResponse userResponse = userService.userLogin(userLoginRequest);
        CommonResponse response = new CommonResponse(userResponse);
        return ResponseUtilities.createSuccessResponse(response);
    }

    @PostMapping("/validateOTP")
    public CommonResponse<ValidateOtpResponse> validateOTP(@RequestBody ValidateOtpRequest request) {
        CommonResponse commonResponse = new CommonResponse<>(userService.validateOTP(request));
        return ResponseUtilities.createSuccessResponse(commonResponse);
    }
@PutMapping("/updateProfile")
    public CommonResponse<CreateProfileUpdateResponse>updateProfile(@Valid @RequestBody CreateProfileUpdateRequest createUpdateProfileRequest) {
    CommonResponse commonResponse = new CommonResponse<>(userService.updateProfile(createUpdateProfileRequest));
    return ResponseUtilities.createSuccessResponse(commonResponse);
}
}
