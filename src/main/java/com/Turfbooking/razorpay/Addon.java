package com.Turfbooking.razorpay;

import lombok.NoArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class Addon extends Entity {

    public Addon(JSONObject jsonObject) {
        super(jsonObject);
    }
}
