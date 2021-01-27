package com.Turfbooking.controller;

import com.Turfbooking.models.response.CommonResponse;
import com.Turfbooking.razorpay.RazorpayException;
import com.Turfbooking.service.RazorPayService;
import com.Turfbooking.utils.ResponseUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
public class RazorPayController {

    @Autowired
    RazorPayService razorPayService;

    @GetMapping("/details")
    CommonResponse getPaymentDetails(@RequestParam String transactionId) throws RazorpayException {
        CommonResponse response = new CommonResponse(razorPayService.getTransactionDetails(transactionId));
        return ResponseUtilities.createSuccessResponse(response);
    }

}
