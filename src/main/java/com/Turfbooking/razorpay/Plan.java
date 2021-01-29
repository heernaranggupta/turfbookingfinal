package com.Turfbooking.razorpay;

import lombok.NoArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class Plan extends Entity {

    public Plan(JSONObject jsonObject) {
        super(jsonObject);
    }
}
