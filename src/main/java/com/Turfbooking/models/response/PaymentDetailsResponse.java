package com.Turfbooking.models.response;

import com.Turfbooking.documents.PaymentDetails;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PaymentDetailsResponse {

    private String id;
    private String userPhoneNumber;
    private String orderId;
    private String transactionId;

    public PaymentDetailsResponse(PaymentDetails paymentDetails) {
        this.id = paymentDetails.getId();
        this.userPhoneNumber = paymentDetails.getUserPhoneNumber();
        this.orderId = paymentDetails.getOrderId();
        this.transactionId = paymentDetails.getTransactionId();
    }
}
