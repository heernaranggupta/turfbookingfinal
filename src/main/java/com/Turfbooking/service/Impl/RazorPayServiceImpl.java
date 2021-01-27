package com.Turfbooking.service.Impl;

import com.Turfbooking.documents.PaymentDetails;
import com.Turfbooking.exception.GeneralException;
import com.Turfbooking.razorpay.Payment;
import com.Turfbooking.razorpay.RazorpayClient;
import com.Turfbooking.razorpay.RazorpayException;
import com.Turfbooking.razorpay.response.PaymentResponse;
import com.Turfbooking.repository.PaymentRepository;
import com.Turfbooking.service.RazorPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RazorPayServiceImpl implements RazorPayService {

    @Value("${razorpay.key}")
    private String razorPayKey;
    @Value("${razorpay.secret}")
    private String razorPaySecret;

    private PaymentRepository paymentRepository;

    @Autowired
    public RazorPayServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    private RazorpayClient initRazorPayClient() throws GeneralException, RazorpayException {
        RazorpayClient razorpayClient = new RazorpayClient(razorPayKey, razorPaySecret);
        return razorpayClient;
    }

    @Override
    public PaymentResponse getTransactionDetails(String orderID) throws RazorpayException {
        PaymentDetails paymentDetails = paymentRepository.findByOrderId(orderID);
        RazorpayClient razorpayClient = this.initRazorPayClient();
        Payment payment = razorpayClient.Payments.fetch(paymentDetails.getTransactionId());
        PaymentResponse response = new PaymentResponse(payment);
        return response;
    }


}
