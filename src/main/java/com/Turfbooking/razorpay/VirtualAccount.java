package com.Turfbooking.razorpay;

import lombok.NoArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class VirtualAccount extends Entity {

    public VirtualAccount(JSONObject jsonObject) {
        super(jsonObject);
    }
}
