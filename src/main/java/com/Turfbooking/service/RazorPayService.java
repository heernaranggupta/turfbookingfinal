package com.Turfbooking.service;

import com.Turfbooking.razorpay.RazorpayException;
import com.Turfbooking.razorpay.response.PaymentResponse;
import com.Turfbooking.razorpay.response.RefundResponse;

import java.util.List;

public interface RazorPayService {

    PaymentResponse getTransactionDetails(String transactionId) throws RazorpayException;

    RefundResponse initRefund(String orderId, String amount) throws RazorpayException;

    List<RefundResponse> getRefundDetails(String orderId, String refundId) throws RazorpayException;
}
