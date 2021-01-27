package com.Turfbooking.razorpay;

import lombok.NoArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class Invoice extends Entity {

    public Invoice(JSONObject jsonObject) {
        super(jsonObject);
    }
}
