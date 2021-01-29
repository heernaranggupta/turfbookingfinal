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
    CommonResponse getPaymentDetails(@RequestParam String orderId) throws RazorpayException {
        CommonResponse response = new CommonResponse(razorPayService.getTransactionDetails(orderId));
        return ResponseUtilities.createSuccessResponse(response);
    }

    @GetMapping("/refund")
    CommonResponse refund(@RequestParam String orderId, @RequestParam(required = false) String amount) throws RazorpayException {
        CommonResponse response = new CommonResponse(razorPayService.initRefund(orderId, amount));
        return ResponseUtilities.createSuccessResponse(response);
    }

    @GetMapping("/refund-details")
    CommonResponse getRefundDetail(@RequestParam String orderId, @RequestParam(required = false) String refundId) throws RazorpayException {
        CommonResponse response = new CommonResponse(razorPayService.getRefundDetails(orderId, refundId));
        return ResponseUtilities.createSuccessResponse(response);
    }

}
