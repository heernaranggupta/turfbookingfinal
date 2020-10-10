package com.Turfbooking.controller;

import com.Turfbooking.models.request.CreateUserRequest;
import com.Turfbooking.models.request.UserLoginRequest;
import com.Turfbooking.models.response.AllBookedSlotByUserResponse;
import com.Turfbooking.models.response.CommonResponse;
import com.Turfbooking.models.response.CreateUserLoginResponse;
import com.Turfbooking.models.response.CreateUserResponse;
import com.Turfbooking.models.response.UserResponse;
import com.Turfbooking.repository.TimeSlotRepository;
import com.Turfbooking.service.UserService;
import com.Turfbooking.utils.ResponseUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public CommonResponse<CreateUserResponse> createUser(@RequestBody @Valid CreateUserRequest createUserRequest){
        CreateUserResponse userResponse = userService.createNewUser(createUserRequest);
        CommonResponse response = new CommonResponse<>(userResponse);
        return ResponseUtilities.createSuccessResponse(response);
    }

    @PostMapping("/login")
    public CommonResponse<UserResponse> userLogin(@Valid @RequestBody UserLoginRequest userLoginRequest){
        CreateUserLoginResponse userResponse = userService.userLogin(userLoginRequest);
        CommonResponse response = new CommonResponse(userResponse);
        return ResponseUtilities.createSuccessResponse(response);
    }

    @PostMapping("/getAllSlots")
    public CommonResponse<AllBookedSlotByUserResponse> allBookedSlots(@RequestParam String userId){
        CommonResponse response = new CommonResponse(userService.getAllBookedSlots(userId));
        return ResponseUtilities.createSuccessResponse(response);
    }
}
