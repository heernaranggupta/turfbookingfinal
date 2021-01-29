package com.Turfbooking.razorpay;

import lombok.NoArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class Transfer extends Entity {

    public Transfer(JSONObject jsonObject) {
        super(jsonObject);
    }
}
