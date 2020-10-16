package com.Turfbooking.controller;

import com.Turfbooking.models.request.BookTimeSlotRequest;
import com.Turfbooking.models.request.BusinessViewAllBookingRequest;
import com.Turfbooking.models.request.CreateBusinessLoginRequest;
import com.Turfbooking.models.request.CreateUpdatePasswordRequest;
import com.Turfbooking.models.request.GetAllSlotsRequest;
import com.Turfbooking.models.request.UpdateBusinessRequest;
import com.Turfbooking.models.response.BookTimeSlotResponse;
import com.Turfbooking.models.response.BusinessViewAllBookingResponse;
import com.Turfbooking.models.response.CommonResponse;
import com.Turfbooking.models.response.CreateBusinessLoginResponse;
import com.Turfbooking.models.response.CreateBusinessUpdateResponse;
import com.Turfbooking.models.response.CreatePasswordResponse;
import com.Turfbooking.service.BusinessService;
import com.Turfbooking.utils.ResponseUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/business")
public class BusinessController {

    private BusinessService businessService;

    @Autowired
    public BusinessController(BusinessService businessService) {
        this.businessService = businessService;
    }

    /*Juhi :: TODO:: 1. create api like this to make slot unavailable
    * Juhi :: TODO:: 2. create api to get all bookings - filter using query parameter; status and date; default 1 week from now only confirmed bookings;
*/
    @PostMapping("/book-slot")
    public CommonResponse<BookTimeSlotResponse> bookSlot(@Valid @RequestBody BookTimeSlotRequest bookTimeSlotRequest) {
        CommonResponse response = new CommonResponse<>(businessService.bookSlot(bookTimeSlotRequest));
        return ResponseUtilities.createSuccessResponse(response);
    }

    @PostMapping("/all-slots")
    public CommonResponse getAllSlots(@Valid @RequestBody GetAllSlotsRequest getAllSlotsRequest) {
        CommonResponse commonResponse = new CommonResponse(businessService.getAllSlots(getAllSlotsRequest));
        return ResponseUtilities.createSuccessResponse(commonResponse);
    }

    @PostMapping("/login")
    public CommonResponse<CreateBusinessLoginResponse> businessLogin(@RequestBody @Valid CreateBusinessLoginRequest request) {
        CommonResponse commonResponse = new CommonResponse(businessService.businessLogin(request));
        return ResponseUtilities.createSuccessResponse(commonResponse);
    }

    @PostMapping("/reset-password")
    public CommonResponse<CreatePasswordResponse> resetPassword(@RequestBody @Valid CreateUpdatePasswordRequest createUpdatePasswordRequest) {
        CommonResponse commonResponse = new CommonResponse(businessService.resetPassword(createUpdatePasswordRequest));
        return ResponseUtilities.createSuccessResponse(commonResponse);
    }

    //not required in minimum viable product
    @PutMapping("/update")
    public CommonResponse<CreateBusinessUpdateResponse> update(@RequestBody UpdateBusinessRequest updateBusinessRequest) {
        CommonResponse commonResponse = new CommonResponse(businessService.updateBusiness(updateBusinessRequest));
        return ResponseUtilities.createSuccessResponse(commonResponse);
    }

    @PostMapping("/view-all-bookings")
    public CommonResponse<List<BookTimeSlotResponse>> viewAllBooking(@RequestBody BusinessViewAllBookingRequest businessViewAllBookingRequest) {
        CommonResponse commonResponse = new CommonResponse(businessService.viewAllBooking(businessViewAllBookingRequest));
        return ResponseUtilities.createSuccessResponse(commonResponse);
    }
}