package com.Turfbooking.service.Impl;

import com.Turfbooking.documents.Payment;
import com.Turfbooking.exception.GeneralException;
import com.Turfbooking.models.response.PaymentResponse;
import com.Turfbooking.repository.PaymentRepository;
import com.Turfbooking.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {

    private PaymentRepository paymentRepository;

    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public String addPaymentDetails(String transactionId, String orderId, String userPhoneNumber) throws GeneralException {
        Payment savePayment = Payment.builder()
                .transactionId(transactionId)
                .orderId(orderId)
                .userPhoneNumber(userPhoneNumber)
                .build();

        Payment savedPayment = paymentRepository.save(savePayment);
        if (null != savedPayment) {
            return savedPayment.getId(); //paymentId
        } else {
            throw new GeneralException("Error in saving payment details", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public PaymentResponse getPaymentDetails(String paymentId) throws GeneralException {
        Payment isPaymentExist = paymentRepository.findByPaymentId(paymentId);
        if (isPaymentExist != null) {
            PaymentResponse response = new PaymentResponse(isPaymentExist);
            return response;
        } else {
            throw new GeneralException("Payment not exist with id : " + paymentId, HttpStatus.NOT_FOUND);
        }
    }

}
