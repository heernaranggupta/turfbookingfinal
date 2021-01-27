package com.Turfbooking.razorpay;

import lombok.NoArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class Customer extends Entity {

    public Customer(JSONObject jsonObject) {
        super(jsonObject);
    }
}
