package com.Turfbooking.models.response;

import com.Turfbooking.documents.Cart;
import com.Turfbooking.models.common.Slot;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {

    private String _cartId;
    private List<Slot> selectedSlots;
    private Double cartTotal;
    private LocalDateTime timeStamp;


    public CartResponse(Cart cart) {
        this._cartId = cart.get_cartId();
        this.selectedSlots = cart.getSelectedSlots();
        this.cartTotal = cart.getCartTotal();
        this.timeStamp = cart.getTimeStamp();
    }

}
