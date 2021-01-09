package com.Turfbooking.models.response;

import com.Turfbooking.documents.PaymentDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDetailsResponse {
    private String _id;
    private String paymentId;
    private String userPhoneNumber;
    private String bookingId;

    public PaymentDetailsResponse(PaymentDetails paymentDetails)
    {
        this._id = paymentDetails.get_id();
        this.paymentId = paymentDetails.getPaymentId();
        this.userPhoneNumber = paymentDetails.getUserPhoneNumber();
        this.bookingId = paymentDetails.getBookingId();
    }
}
