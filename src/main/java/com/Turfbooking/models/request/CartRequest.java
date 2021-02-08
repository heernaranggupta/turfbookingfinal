package com.Turfbooking.models.request;

import com.Turfbooking.models.common.Slot;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Valid
@Getter
@Setter
public class CartRequest {

    @NotEmpty(message = "cart id should not be null")
    private String cartId;
    private String userPhoneNumber;
    private List<Slot> selectedSlots;

}
