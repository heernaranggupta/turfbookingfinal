package com.Turfbooking.models.request;

import com.Turfbooking.models.common.Slot;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CartRequest {

    private String cartId;
    private String userPhoneNumber;
    private List<Slot> selectedSlots;

}
