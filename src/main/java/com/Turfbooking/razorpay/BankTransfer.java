package com.Turfbooking.razorpay;

import lombok.NoArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class BankTransfer extends Entity {

    public BankTransfer(JSONObject jsonObject) {
        super(jsonObject);
    }
}
