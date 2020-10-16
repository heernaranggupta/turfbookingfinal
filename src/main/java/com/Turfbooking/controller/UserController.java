package com.Turfbooking.controller;

import com.Turfbooking.models.request.BookTimeSlotRequest;
import com.Turfbooking.models.request.CreateUserRequest;
import com.Turfbooking.models.request.GetAllSlotsRequest;
import com.Turfbooking.models.request.CustomerProfileUpdateRequest;
import com.Turfbooking.models.request.UpdateBookedTimeSlotRequest;
import com.Turfbooking.models.request.UserLoginRequest;
import com.Turfbooking.models.request.ValidateOtpRequest;
import com.Turfbooking.models.response.AllBookedSlotByUserResponse;
import com.Turfbooking.models.response.BookTimeSlotResponse;
import com.Turfbooking.models.response.CommonResponse;
import com.Turfbooking.models.response.CreateUserLoginResponse;
import com.Turfbooking.models.response.CreateUserResponse;
import com.Turfbooking.models.response.CustomerProfileUpdateResponse;
import com.Turfbooking.models.response.UserResponse;
import com.Turfbooking.models.response.ValidateOtpResponse;
import com.Turfbooking.service.UserService;
import com.Turfbooking.utils.ResponseUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    @PostMapping("/sign-up")
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

    @PostMapping("/validate-otp")
    public CommonResponse<ValidateOtpResponse> validateOTP(@RequestBody ValidateOtpRequest request) {
        CommonResponse commonResponse = new CommonResponse<>(userService.validateOTP(request));
        return ResponseUtilities.createSuccessResponse(commonResponse);
    }

    @PutMapping("/update-profile")
    public CommonResponse<CustomerProfileUpdateResponse> updateProfile(@Valid @RequestBody CustomerProfileUpdateRequest customerProfileUpdateRequest) {
        CustomerProfileUpdateResponse customerProfileUpdateResponse = userService.updateProfile(customerProfileUpdateRequest);
        CommonResponse commonResponse = new CommonResponse<>(customerProfileUpdateRequest);
        return ResponseUtilities.createSuccessResponse(commonResponse);
    }

    @CacheEvict(
            value = "listOfSlotsByTurfIdAndDate",
            key = "#bookTimeSlotRequest.turfId.concat('-').concat(#bookTimeSlotRequest.date.toString())",
            condition = "#bookTimeSlotRequest.turfId != null")
    //we need to get booking id,date for removing cache ,as we need key which is turfId and date
    @GetMapping("cancel-booking")
    public CommonResponse cancelBookedSlot(@RequestParam String bookingId) {
        BookTimeSlotResponse timeSlotResponse = userService.cancelBookedSlot(bookingId);
        CommonResponse response = new CommonResponse(timeSlotResponse);
        return response;
    }

    @CacheEvict(
            value = "listOfSlotsByTurfIdAndDate",
            key = "#bookTimeSlotRequest.turfId.concat('-').concat(#bookTimeSlotRequest.date.toString())",
            condition = "#bookTimeSlotRequest.turfId != null")
    @PostMapping("/book-slot")
    public CommonResponse<BookTimeSlotResponse> bookSlot(@Valid @RequestBody BookTimeSlotRequest bookTimeSlotRequest){
        CommonResponse response = new CommonResponse<>(userService.bookSlot(bookTimeSlotRequest));
        return ResponseUtilities.createSuccessResponse(response);
    }

    @CacheEvict(
            value = "listOfSlotsByTurfIdAndDate",
            key = "#updateBookedTimeSlotRequest.turfId.concat('-').concat(#updateBookedTimeSlotRequest.date.toString())",
            condition = "#updateBookedTimeSlotRequest.turfId != null")
    @PostMapping("update-booking")
    public CommonResponse updateBookedSlot(@Valid @RequestBody UpdateBookedTimeSlotRequest updateBookedTimeSlotRequest){
        BookTimeSlotResponse timeSlotResponse = userService.updateBookedSlot(updateBookedTimeSlotRequest);
        CommonResponse response = new CommonResponse(timeSlotResponse);
        return ResponseUtilities.createSuccessResponse(response);
    }

    //view user booking history
    @GetMapping("/booking-history")
    public CommonResponse<AllBookedSlotByUserResponse> allBookedSlots(@RequestParam String userId) {
        CommonResponse response = new CommonResponse(userService.getAllBookedSlots(userId));
        return ResponseUtilities.createSuccessResponse(response);
    }

    @PostMapping("/get-all-slots-by-date")
    public CommonResponse getAllSlotsByDate(@Valid @RequestBody GetAllSlotsRequest getAllSlotsRequest) {
        CommonResponse commonResponse = new CommonResponse(userService.getAllSlotsByDate(getAllSlotsRequest));
        return ResponseUtilities.createSuccessResponse(commonResponse);
    }
}
