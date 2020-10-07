package com.Turfbooking.documents;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document
@CompoundIndex(def = "{'phoneNumber':1, 'otp':-1}", name = "ph_otp_index", background = true)

public class Otp {

    @Id
    private String _id;
    @Indexed(name = "otp_phone_number", direction = IndexDirection.DESCENDING, background = true)
    private String phoneNumber;
    private Integer otp;
    private LocalDateTime timeTillActive;
    private String otpStatusActive;

    public Otp(String phoneNumber, Integer otp, LocalDateTime timeTillActive, String otpStatusActive) {
        this.phoneNumber = phoneNumber;
        this.otp = otp;
        this.timeTillActive = timeTillActive;
        this.otpStatusActive = otpStatusActive;
    }
}
