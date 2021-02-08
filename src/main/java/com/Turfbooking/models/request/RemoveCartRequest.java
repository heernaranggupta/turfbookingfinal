package com.Turfbooking.models.request;

import com.Turfbooking.models.common.Slot;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

@Valid
@Getter
@Setter
public class RemoveCartRequest {

    @NotEmpty(message = "cart id should be not null")
    private String cartId;
    private String userPhoneNumber;
    private Slot removeSlot;

}
