package com.Turfbooking.service;

import com.Turfbooking.models.response.PaymentDetailsResponse;

public interface PaymentService {

    String addPaymentDetails(String transactionId, String orderId, String userPhoneNumber);

    PaymentDetailsResponse getPaymentDetails(String paymentId);

}
