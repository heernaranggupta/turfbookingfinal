package com.Turfbooking.service;

import com.Turfbooking.models.response.PaymentResponse;

public interface PaymentService {

    String addPaymentDetails(String transactionId, String orderId, String userPhoneNumber);

    PaymentResponse getPaymentDetails(String paymentId);

}
