package com.Turfbooking.controller;

import com.Turfbooking.models.request.BookTimeSlotRequest;
import com.Turfbooking.models.response.BookTimeSlotResponse;
import com.Turfbooking.models.response.CommonResponse;
import com.Turfbooking.service.BusinessService;
import com.Turfbooking.utils.ResponseUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
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
    @PostMapping("/getSlotList")
    public CommonResponse getSlotList(){
        CommonResponse commonResponse = new CommonResponse(null);
        return commonResponse;
    }



}
