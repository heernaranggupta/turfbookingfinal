package com.Turfbooking.service.Impl;

import com.Turfbooking.documents.PaymentDetails;
import com.Turfbooking.exception.GeneralException;
import com.Turfbooking.razorpay.Payment;
import com.Turfbooking.razorpay.RazorpayClient;
import com.Turfbooking.razorpay.RazorpayException;
import com.Turfbooking.razorpay.Refund;
import com.Turfbooking.razorpay.response.PaymentResponse;
import com.Turfbooking.razorpay.response.RefundResponse;
import com.Turfbooking.repository.PaymentRepository;
import com.Turfbooking.service.RazorPayService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public RefundResponse initRefund(String orderId, String amount) throws RazorpayException, GeneralException {
        RazorpayClient razorpayClient = this.initRazorPayClient();
        PaymentDetails paymentDetails = paymentRepository.findByOrderId(orderId);
        String id = paymentDetails.getTransactionId();
        Payment payment = razorpayClient.Payments.fetch(id);
        if (payment.get("status").toString().equalsIgnoreCase("authorized")) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("amount", payment.get("amount").toString());
            payment = razorpayClient.Payments.capture(id, jsonObject);
        }
        JSONObject amountObject = new JSONObject();
        amountObject.put("amount", Float.parseFloat(amount));
        if (false) { //if capture is false and status is authorized
            razorpayClient.Payments.capture(id, amountObject);
        }
        Refund refund = null;
        if (null != amount) {
            refund = razorpayClient.Payments.refund(id, amountObject);
        } else {
            refund = razorpayClient.Payments.refund(id);
        }
        System.out.println(refund.toString());
        RefundResponse response = new RefundResponse(refund);
        return response;
    }

    @Override
    public List<RefundResponse> getRefundDetails(String orderId, String refundId) throws GeneralException, RazorpayException {
        RazorpayClient razorpayClient = this.initRazorPayClient();
        PaymentDetails paymentDetails = paymentRepository.findByOrderId(orderId);
        String id = paymentDetails.getTransactionId();
        JSONObject object = new JSONObject();
        object.put("amount", 10 * 100);
        razorpayClient.Payments.capture(id, object);
        List<RefundResponse> refundResponses = new ArrayList<>();
        if (null != refundId) {
            Refund refund = razorpayClient.Payments.fetchRefund(id, refundId);
            RefundResponse response = new RefundResponse(refund);
            refundResponses.add(response);
        } else {
            List<Refund> refundList = razorpayClient.Payments.fetchAllRefunds(id);
            refundList.stream().forEach(refund -> {
                RefundResponse response = new RefundResponse(refund);
                refundResponses.add(response);
            });
        }
        return refundResponses;
    }


}
