package com.Turfbooking.controller;

import com.Turfbooking.models.request.GenerateOtpRequest;
import com.Turfbooking.models.request.OrderRequest;
import com.Turfbooking.models.request.SlotValidationRequest;
import com.Turfbooking.models.request.ValidateOtpRequest;
import com.Turfbooking.models.response.CommonResponse;
import com.Turfbooking.models.response.CreateResponse;
import com.Turfbooking.models.response.OrderResponse;
import com.Turfbooking.models.response.ValidateOtpResponse;
import com.Turfbooking.service.CommonService;
import com.Turfbooking.service.PaymentService;
import com.Turfbooking.utils.ResponseUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import java.io.IOException;

@RestController
@RequestMapping("/common")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CommonController {

    private final CommonService commonService;
    private final PaymentService paymentService;

    @Autowired
    public CommonController(CommonService commonService, PaymentService paymentService) {
        this.commonService = commonService;
        this.paymentService = paymentService;
    }

    @PostMapping("/generate-otp")
    public CommonResponse<CreateResponse> generateOTP(@RequestBody GenerateOtpRequest request) throws IOException, MessagingException {
        CommonResponse commonResponse = new CommonResponse<>(commonService.generateOtp(request));
        return ResponseUtilities.createSuccessResponse(commonResponse);
    }

    @PostMapping("/validate-otp")
    public CommonResponse<ValidateOtpResponse> validateOTP(@RequestBody ValidateOtpRequest request) {
        CommonResponse commonResponse = new CommonResponse<>(commonService.validateOTP(request));
        return ResponseUtilities.createSuccessResponse(commonResponse);
    }

    @PostMapping("/order")
    public CommonResponse<OrderResponse> placeOrder(@RequestBody OrderRequest orderRequest, Authentication authentication) {
        orderRequest.setUserId(authentication.getName());
        CommonResponse response = new CommonResponse<>(commonService.placeOrder(orderRequest, authentication.getName()));
        return ResponseUtilities.createSuccessResponse(response);
    }

    @PostMapping("/validate")
    public CommonResponse slotAvailableOrNot(@RequestBody SlotValidationRequest slotValidationRequest, Authentication authentication) {
        CommonResponse response = new CommonResponse(commonService.validateSlotAvailableOrNot(slotValidationRequest, authentication.getName()));
        return ResponseUtilities.createSuccessResponse(response);
    }

    @GetMapping("/payment-details")
    public CommonResponse getPaymentDetails(@RequestParam String paymentID) {
        CommonResponse response = new CommonResponse(paymentService.getPaymentDetails(paymentID));
        return ResponseUtilities.createSuccessResponse(response);
    }

    @GetMapping("/order/slot-list")
    public CommonResponse getAllSlotsOfOrderId(@RequestParam String orderId) {
        CommonResponse response = new CommonResponse(commonService.getAllBookedSlotsByOrderId(orderId));
        return ResponseUtilities.createSuccessResponse(response);
    }

}
