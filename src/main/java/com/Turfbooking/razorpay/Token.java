package com.Turfbooking.razorpay;

import lombok.NoArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class Token extends Entity {

    public Token(JSONObject jsonObject) {
        super(jsonObject);
    }
}