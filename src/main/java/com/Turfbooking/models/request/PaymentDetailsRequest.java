package com.Turfbooking.models.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentDetailsRequest {

    private String paymentId;
    private String userPhoneNumber;
    private String bookingId;

}
