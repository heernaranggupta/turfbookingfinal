package com.Turfbooking.razorpay;

import lombok.NoArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class Subscription extends Entity {

    public Subscription(JSONObject jsonObject) {
        super(jsonObject);
    }
}
