package com.Turfbooking.service.Impl;

import com.Turfbooking.documents.PaymentDetails;
import com.Turfbooking.exception.GeneralException;
import com.Turfbooking.models.response.PaymentDetailsResponse;
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
        PaymentDetails savePaymentDetails = PaymentDetails.builder()
                .transactionId(transactionId)
                .orderId(orderId)
                .userPhoneNumber(userPhoneNumber)
                .build();

        PaymentDetails savedPaymentDetails = paymentRepository.save(savePaymentDetails);
        if (null != savedPaymentDetails) {
            return savedPaymentDetails.getId(); //paymentId
        } else {
            throw new GeneralException("Error in saving payment details", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public PaymentDetailsResponse getPaymentDetails(String orderId) throws GeneralException {
        PaymentDetails isPaymentDetailsExist = paymentRepository.findByOrderId(orderId);
        if (isPaymentDetailsExist != null) {
            PaymentDetailsResponse response = new PaymentDetailsResponse(isPaymentDetailsExist);
            return response;
        } else {
            throw new GeneralException("PaymentDetails not exist with id : " + orderId, HttpStatus.NOT_FOUND);
        }
    }

}
