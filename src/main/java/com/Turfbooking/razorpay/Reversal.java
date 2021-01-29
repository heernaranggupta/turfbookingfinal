package com.Turfbooking.razorpay;

import lombok.NoArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class Reversal extends Entity {

    public Reversal(JSONObject jsonObject) {
        super(jsonObject);
    }
}
