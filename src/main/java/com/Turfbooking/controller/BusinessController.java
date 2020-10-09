package com.Turfbooking.controller;

import com.Turfbooking.documents.Business;
import com.Turfbooking.models.request.BookTimeSlotRequest;
import com.Turfbooking.models.request.UpdateBusinessRequest;
import com.Turfbooking.models.request.GetAllSlotsRequest;
import com.Turfbooking.models.response.BookTimeSlotResponse;
import com.Turfbooking.models.response.BusinessResponse;
import com.Turfbooking.models.response.CommonResponse;
import com.Turfbooking.models.request.CreateBusinessLoginRequest;
import com.Turfbooking.models.request.CreateUpdatePasswordRequest;
import com.Turfbooking.models.response.CreateBusinessLoginResponse;
import com.Turfbooking.models.response.CreateBusinessUpdateResponse;
import com.Turfbooking.models.response.CreatePasswordResponse;
import com.Turfbooking.service.BusinessService;
import com.Turfbooking.utils.ResponseUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/business")
public class BusinessController {

    private BusinessService businessService;

    @Autowired
    public BusinessController(BusinessService businessService) {
        this.businessService = businessService;
    }

    @PostMapping("/book-slot")
    public CommonResponse<BookTimeSlotResponse> bookSlot(@Valid @RequestBody BookTimeSlotRequest bookTimeSlotRequest){
        CommonResponse response = new CommonResponse<>(businessService.bookSlot(bookTimeSlotRequest));
        return ResponseUtilities.createSuccessResponse(response);
    }

    //all booked slot by date
    @PostMapping("/getAllSlots")
    public CommonResponse getAllSlots(@Valid @RequestBody GetAllSlotsRequest getAllSlotsRequest){
        CommonResponse commonResponse = new CommonResponse(businessService.getAllSlots(getAllSlotsRequest));
        return ResponseUtilities.createSuccessResponse(commonResponse);
    }

    @PostMapping("/login")
    public CommonResponse<CreateBusinessLoginResponse> businessLogin(@RequestBody @Valid CreateBusinessLoginRequest request) {
        CommonResponse commonResponse = new CommonResponse(businessService.businessLogin(request));
        return ResponseUtilities.createSuccessResponse(commonResponse);
    }


    @PostMapping("/resetPassword")
    public CommonResponse<CreatePasswordResponse> resetPassword(@RequestBody @Valid CreateUpdatePasswordRequest createUpdatePasswordRequest) {
        CommonResponse commonResponse = new CommonResponse(businessService.resetPassword(createUpdatePasswordRequest));
        return ResponseUtilities.createSuccessResponse(commonResponse);
    }

    @PutMapping("/update")
    public CommonResponse<CreateBusinessUpdateResponse> update(@RequestBody UpdateBusinessRequest updateBusinessRequest) {
        CommonResponse commonResponse = new CommonResponse(businessService.updateBusiness(updateBusinessRequest));
        return ResponseUtilities.createSuccessResponse(commonResponse);
    }

}