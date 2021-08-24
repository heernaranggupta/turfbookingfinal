package com.Turfbooking.service;

import com.Turfbooking.models.request.GenerateOtpRequest;
import com.Turfbooking.models.request.OrderRequest;
import com.Turfbooking.models.request.SlotValidationRequest;
import com.Turfbooking.models.request.ValidateOtpRequest;
import com.Turfbooking.models.response.CreateResponse;
import com.Turfbooking.models.response.OrderResponse;
import com.Turfbooking.models.response.SlotValidationResponse;
import com.Turfbooking.models.response.TimeSlotResponse;
import com.Turfbooking.models.response.ValidateOtpResponse;
import com.Turfbooking.razorpay.RazorpayException;

import java.util.List;

public interface CommonService {

    CreateResponse generateOtp(GenerateOtpRequest otpRequest);

    ValidateOtpResponse validateOTP(ValidateOtpRequest validateOtpRequest);

    OrderResponse placeOrder(OrderRequest orderRequest, String username) throws RazorpayException;

    SlotValidationResponse validateSlotAvailableOrNot(SlotValidationRequest slotValidationRequest, String userID);

    List<TimeSlotResponse> getAllBookedSlotsByOrderId(String orderId);

}
