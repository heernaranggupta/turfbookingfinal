package com.Turfbooking.controller;

import com.Turfbooking.models.request.BookTimeSlotRequest;
import com.Turfbooking.models.request.CancelOrUnavailableSlotRequest;
import com.Turfbooking.models.request.CreateBusinessLoginRequest;
import com.Turfbooking.models.request.CreateRescheduleBookingRequest;
import com.Turfbooking.models.request.CreateUpdatePasswordRequest;
import com.Turfbooking.models.request.GetAllSlotsBusinessRequest;
import com.Turfbooking.models.response.UpdateBusinessConfigResponse;
import com.Turfbooking.models.request.UpdateBusinessRequest;
import com.Turfbooking.models.response.BookTimeSlotResponse;
import com.Turfbooking.models.response.CommonResponse;
import com.Turfbooking.models.response.CreateBusinessLoginResponse;
import com.Turfbooking.models.response.CreateBusinessUpdateResponse;
import com.Turfbooking.models.response.CreatePasswordResponse;
import com.Turfbooking.models.response.RescheduleBookingResponse;
import com.Turfbooking.models.request.UpdateBusinessConfigRequest;
import com.Turfbooking.service.BusinessService;
import com.Turfbooking.utils.ResponseUtilities;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/business")
public class BusinessController {

    private BusinessService businessService;

    @Autowired
    public BusinessController(BusinessService businessService) {
        this.businessService = businessService;
    }
//
//    @CacheEvict(
//            value = "listOfSlotsByTurfIdAndDate",
//            key = "#bookTimeSlotRequest.turfId.concat('-').concat(#bookTimeSlotRequest.date.toString())",
//            condition = "#bookTimeSlotRequest.turfId != null")
    @PostMapping("/book-slot")
    public CommonResponse<BookTimeSlotResponse> bookSlot(@Valid @RequestBody BookTimeSlotRequest bookTimeSlotRequest) {
        CommonResponse response = new CommonResponse<>(businessService.bookSlot(bookTimeSlotRequest));
        return ResponseUtilities.createSuccessResponse(response);
    }

    //cache - change on booking
    //all slots - available and unavailable by date
//    @Cacheable(
//            value = "listOfSlotsByTurfIdAndDate",
//            key = "#getAllSlotsRequest.turfId.concat('-').concat(#getAllSlotsRequest.date.toString())",
//            condition = "#getAllSlotsRequest.turfId != null")
    @PostMapping("/all-slots")
    public CommonResponse getAllSlots(@Valid @RequestBody GetAllSlotsBusinessRequest getAllSlotsBusinessRequest) {
        log.info("Get all slots method executed. : "+ getAllSlotsBusinessRequest.getTurfIds().get(1)+"--"+getAllSlotsBusinessRequest.getDate());
        CommonResponse commonResponse = new CommonResponse(businessService.getAllSlots(getAllSlotsBusinessRequest));
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

    @PostMapping("/slot/make-unavailable")
    public CommonResponse makeSlotUnavailable(@RequestBody CancelOrUnavailableSlotRequest cancelOrUnavailableSlotRequest) {
        CommonResponse response = new CommonResponse(businessService.makeSlotUnavailable(cancelOrUnavailableSlotRequest));
        return ResponseUtilities.createSuccessResponse(response);
    }
    @PostMapping("/reschedule-booking")
    public CommonResponse<RescheduleBookingResponse> rescheduleBooking (@RequestBody CreateRescheduleBookingRequest createRescheduleBookingRequest) {
        CommonResponse commonResponse = new CommonResponse(businessService.rescheduleBooking(createRescheduleBookingRequest));
        return ResponseUtilities.createSuccessResponse(commonResponse);
    }

    @PostMapping("/cancel-booking")
    public CommonResponse cancelBooking(@RequestBody CancelOrUnavailableSlotRequest cancelOrUnavailableSlotRequest) {
        BookTimeSlotResponse timeSlotResponse = businessService.cancelBooking(cancelOrUnavailableSlotRequest);
        CommonResponse response = new CommonResponse(timeSlotResponse);
        return response;
    }

    @PostMapping("/update-config")
    public CommonResponse<UpdateBusinessConfigResponse> updateBusinessConfigs(@RequestBody UpdateBusinessConfigRequest updateBusinessConfigRequest){
        CommonResponse response = new CommonResponse<>(businessService.updateBusinessConfig(updateBusinessConfigRequest));
        return ResponseUtilities.createSuccessResponse(response);
    }
}