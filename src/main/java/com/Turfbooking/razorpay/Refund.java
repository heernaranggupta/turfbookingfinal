package com.Turfbooking.razorpay;

import lombok.NoArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class Refund extends Entity {

    public Refund(JSONObject jsonObject) {
        super(jsonObject);
    }
}
