package com.Turfbooking.documents;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Builder
@Document
public class PaymentDetails {

    @Id
    private String id;
    private String userPhoneNumber;
    private String orderId;
    private String transactionId;

    public PaymentDetails(String id, String userPhoneNumber, String orderId, String transactionId) {
        this.id = id;
        this.userPhoneNumber = userPhoneNumber;
        this.orderId = orderId;
        this.transactionId = transactionId;
    }
}
