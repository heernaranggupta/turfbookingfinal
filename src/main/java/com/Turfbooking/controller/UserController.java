package com.Turfbooking.controller;

import com.Turfbooking.models.request.CancelOrUnavailableSlotRequest;
import com.Turfbooking.models.request.CreateUserRequest;
import com.Turfbooking.models.request.CustomerProfileUpdateRequest;
import com.Turfbooking.models.request.GetAllSlotsRequest;
import com.Turfbooking.models.request.UpdateBookedTimeSlotRequest;
import com.Turfbooking.models.request.UserLoginRequest;
import com.Turfbooking.models.response.AllBookedSlotByUserResponse;
import com.Turfbooking.models.response.BookTimeSlotResponse;
import com.Turfbooking.models.response.CommonResponse;
import com.Turfbooking.models.response.CreateUserLoginResponse;
import com.Turfbooking.models.response.CreateUserResponse;
import com.Turfbooking.models.response.CustomerProfileUpdateResponse;
import com.Turfbooking.models.response.UserResponse;
import com.Turfbooking.service.UserService;
import com.Turfbooking.utils.ResponseUtilities;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*", allowedHeaders = "*")
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
    public CommonResponse<CreateUserLoginResponse> userLogin(@Valid @RequestBody UserLoginRequest userLoginRequest) {
        CreateUserLoginResponse userResponse = userService.userLogin(userLoginRequest);
        CommonResponse response = new CommonResponse(userResponse);
        return ResponseUtilities.createSuccessResponse(response);
    }

    @PutMapping("/update-profile")
    public CommonResponse<CustomerProfileUpdateResponse> updateProfile(@Valid @RequestBody CustomerProfileUpdateRequest customerProfileUpdateRequest) {
        CustomerProfileUpdateResponse customerProfileUpdateResponse = userService.updateProfile(customerProfileUpdateRequest);
        CommonResponse commonResponse = new CommonResponse<>(customerProfileUpdateRequest);
        return ResponseUtilities.createSuccessResponse(commonResponse);
    }

    @CacheEvict(
            value = "listOfSlotsByTurfIdAndDate",
            allEntries = true,
            condition = "#cancelOrUnavailableSlotRequest.turfId != null")
    @PostMapping("cancel-booking")
    public CommonResponse cancelBookedSlot(@RequestBody CancelOrUnavailableSlotRequest cancelOrUnavailableSlotRequest) {
        BookTimeSlotResponse timeSlotResponse = userService.cancelBookedSlot(cancelOrUnavailableSlotRequest);
        CommonResponse response = new CommonResponse(timeSlotResponse);
        return response;
    }

    @CacheEvict(
            value = "listOfSlotsByTurfIdAndDate",
            allEntries = true,
            condition = "#updateBookedTimeSlotRequest.turfId != null")
    @PostMapping("update-booking")
    public CommonResponse updateBookedSlot(@Valid @RequestBody UpdateBookedTimeSlotRequest updateBookedTimeSlotRequest) {
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

    @Cacheable(
            value = "listOfSlotsByTurfIdAndDate",
            key = "#getAllSlotsRequest.turfId.concat(#getAllSlotsRequest.date.toString())",
            condition = "#getAllSlotsRequest.date != null")
    @PostMapping("/get-all-slots-by-date")
    public CommonResponse getAllSlotsByDate(@Valid @RequestBody GetAllSlotsRequest getAllSlotsRequest) {
        log.info("Method executed" + getAllSlotsRequest.getTurfId() + "--" + getAllSlotsRequest.getDate());
        CommonResponse commonResponse = new CommonResponse(userService.getAllSlotsByDate(getAllSlotsRequest));
        return ResponseUtilities.createSuccessResponse(commonResponse);
    }

}
