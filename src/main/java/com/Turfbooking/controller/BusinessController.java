package com.Turfbooking.controller;

import com.Turfbooking.models.request.BookTimeSlotRequest;
import com.Turfbooking.models.request.CreateBusinessLoginRequest;
import com.Turfbooking.models.request.CreateUpdatePasswordRequest;
import com.Turfbooking.models.request.GetAllSlotsRequest;
import com.Turfbooking.models.request.UpdateBusinessRequest;
import com.Turfbooking.models.response.BookTimeSlotResponse;
import com.Turfbooking.models.response.CommonResponse;
import com.Turfbooking.models.response.CreateBusinessLoginResponse;
import com.Turfbooking.models.response.CreateBusinessUpdateResponse;
import com.Turfbooking.models.response.CreatePasswordResponse;
import com.Turfbooking.service.BusinessService;
import com.Turfbooking.utils.ResponseUtilities;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
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

    /*Juhi :: TODO:: 1. create api like this to make slot unavailable
    * Juhi :: TODO:: 2. create api to get all bookings - filter using query parameter; status and date; default 1 week from now only confirmed bookings;
      Arpit :: TODO:: 3. Caching
      TODO:: 4. Create business config to store open time; close time and holidays; might need to have it in memory - cache
     TODO :: 5. Create an api to update business config
    * */


    //create api like this to make slot unavailable
    @PostMapping("/book-slot")
    public CommonResponse<BookTimeSlotResponse> bookSlot(@Valid @RequestBody BookTimeSlotRequest bookTimeSlotRequest) {
        CommonResponse response = new CommonResponse<>(businessService.bookSlot(bookTimeSlotRequest));
        return ResponseUtilities.createSuccessResponse(response);
    }

    //cache - change on booking
    //all slots - available and unavailable by date
    @Cacheable(
            value = "listOfSlotsByTurfIdAndDate",
            key = "#getAllSlotsRequest.turfId.concat('-').concat(#getAllSlotsRequest.date)",
            condition = "#getAllSlotsRequest.turfId != null")
    @PostMapping("/all-slots")
    public CommonResponse getAllSlots(@Valid @RequestBody GetAllSlotsRequest getAllSlotsRequest) {
        log.info("Get all slots method executed. : "+ getAllSlotsRequest.getTurfId()+"--"+getAllSlotsRequest.getDate());
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



}