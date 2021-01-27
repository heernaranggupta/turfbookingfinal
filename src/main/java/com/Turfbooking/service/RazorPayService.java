package com.Turfbooking.service;

import com.Turfbooking.razorpay.RazorpayException;
import com.Turfbooking.razorpay.response.PaymentResponse;

public interface RazorPayService {

    PaymentResponse getTransactionDetails(String transactionId) throws RazorpayException;
}
