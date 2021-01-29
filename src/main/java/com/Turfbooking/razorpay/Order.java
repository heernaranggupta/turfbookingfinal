package com.Turfbooking.razorpay;

import lombok.NoArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class Order extends Entity {

    public Order(JSONObject jsonObject) {
        super(jsonObject);
    }
}
