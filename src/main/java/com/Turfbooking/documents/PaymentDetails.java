package com.Turfbooking.documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDetails {

    @Id
    private String _id;
    private String paymentId;
    private String userPhoneNumber;
    private String bookingId;

}
