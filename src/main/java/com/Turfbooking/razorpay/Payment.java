package com.Turfbooking.razorpay;

import lombok.NoArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class Payment extends Entity {

  public Payment(JSONObject jsonObject) {
    super(jsonObject);
  }
}
