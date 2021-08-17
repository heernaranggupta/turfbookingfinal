package com.Turfbooking.controller;

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
import com.Turfbooking.models.response.RescheduleBookingResponse;
import com.Turfbooking.models.response.TimeSlotResponse;
import com.Turfbooking.service.BusinessService;
import com.Turfbooking.utils.ResponseUtilities;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/business")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class BusinessController {

    private final BusinessService businessService;


    @Autowired
    public BusinessController(BusinessService businessService) {
        this.businessService = businessService;
    }


    
    @PostMapping("/signup")
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    public CommonResponse createNewBusinessUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        CommonResponse response = new CommonResponse(businessService.createBusinessUser(createUserRequest));
        return ResponseUtilities.createSuccessResponse(response);
    }

    @PostMapping("/all-slots")
    public CommonResponse getAllSlots(@Valid @RequestBody GetAllSlotsBusinessRequest getAllSlotsBusinessRequest) {
        CommonResponse commonResponse = new CommonResponse(businessService.getAllSlots(getAllSlotsBusinessRequest));
        return ResponseUtilities.createSuccessResponse(commonResponse);
    }

    @PostMapping("/login")
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    public CommonResponse businessLogin(@RequestBody @Valid UserLoginRequest request) {
        CommonResponse commonResponse = new CommonResponse(businessService.businessLogin(request));
        return ResponseUtilities.createSuccessResponse(commonResponse);
    }

    @PostMapping("/reset-password")
    public CommonResponse<CreatePasswordResponse> resetPassword(@RequestBody @Valid CreateUpdatePasswordRequest createUpdatePasswordRequest) {
        CommonResponse commonResponse = new CommonResponse(businessService.resetPassword(createUpdatePasswordRequest));
        return ResponseUtilities.createSuccessResponse(commonResponse);
    }

    @PutMapping("/update")
    public CommonResponse<CreateBusinessUpdateResponse> update(@RequestBody UpdateBusinessRequest updateBusinessRequest, Authentication authentication) {
        updateBusinessRequest.setPhoneNumber(authentication.getName());
        CommonResponse commonResponse = new CommonResponse(businessService.updateBusiness(updateBusinessRequest));
        return ResponseUtilities.createSuccessResponse(commonResponse);
    }

    @PostMapping("/slot/make-unavailable")
    public CommonResponse makeSlotUnavailable(@RequestBody CancelOrUnavailableSlotRequest cancelOrUnavailableSlotRequest) {
        CommonResponse response = new CommonResponse(businessService.makeSlotUnavailable(cancelOrUnavailableSlotRequest));
        return ResponseUtilities.createSuccessResponse(response);
    }

    @PostMapping("/reschedule-booking")
    public CommonResponse<RescheduleBookingResponse> rescheduleBooking(@RequestBody CreateRescheduleBookingRequest createRescheduleBookingRequest, Authentication authentication) {
        createRescheduleBookingRequest.setUserId(authentication.getName());
        CommonResponse commonResponse = new CommonResponse(businessService.rescheduleBooking(createRescheduleBookingRequest));
        return ResponseUtilities.createSuccessResponse(commonResponse);
    }

    @PostMapping("/cancel-booking")
    public CommonResponse cancelBooking(@RequestBody CancelOrUnavailableSlotRequest cancelOrUnavailableSlotRequest) {
        TimeSlotResponse timeSlotResponse = businessService.cancelBooking(cancelOrUnavailableSlotRequest);
        CommonResponse response = new CommonResponse(timeSlotResponse);
        return response;
    }

    @PostMapping("/view-all-bookings")
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    public CommonResponse<List<TimeSlotResponse>> viewAllBooking(@RequestBody BusinessViewAllBookingRequest businessViewAllBookingRequest) {
        CommonResponse commonResponse = new CommonResponse(businessService.viewAllBooking(businessViewAllBookingRequest));
        return ResponseUtilities.createSuccessResponse(commonResponse);
    }

    @GetMapping("/get-all-business-users")
    public CommonResponse getAllBusinessUsers() {
        CommonResponse response = new CommonResponse(businessService.getAllBusinessUsers());
        return ResponseUtilities.createSuccessResponse(response);
    }
}
