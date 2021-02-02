package com.Turfbooking.documents;

import com.Turfbooking.models.common.Slot;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Document
@AllArgsConstructor
@NoArgsConstructor
public class Cart {

    @Id
    private String _cartId;
    @Indexed
    private String userPhoneNumber;
    private List<Slot> selectedSlots;
    private Double cartTotal;
    private LocalDateTime timeStamp;

    public Cart(String userPhoneNumber, List<Slot> selectedSlots, Double cartTotal, LocalDateTime timeStamp) {
        this.userPhoneNumber = userPhoneNumber;
        this.selectedSlots = selectedSlots;
        this.cartTotal = cartTotal;
        this.timeStamp = timeStamp;
    }
}
