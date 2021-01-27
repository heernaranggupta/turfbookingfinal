package com.Turfbooking.razorpay;

import lombok.NoArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class Card extends Entity {

    public Card(JSONObject jsonObject) {
        super(jsonObject);
    }
}
