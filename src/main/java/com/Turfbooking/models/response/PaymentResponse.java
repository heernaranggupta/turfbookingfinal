package com.Turfbooking.models.response;

import com.Turfbooking.documents.Payment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PaymentResponse {

    private String id;
    private String userPhoneNumber;
    private String orderId;
    private String transactionId;

    public PaymentResponse(Payment payment) {
        this.id = payment.getId();
        this.userPhoneNumber = payment.getUserPhoneNumber();
        this.orderId = payment.getOrderId();
        this.transactionId = payment.getTransactionId();
    }
}
