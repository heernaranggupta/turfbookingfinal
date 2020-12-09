package com.Turfbooking.models.request;

import com.Turfbooking.models.common.Slot;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RemoveCartRequest {

    private String cartId;
    private String userPhoneNumber;
    private Slot removeSlot;

}
