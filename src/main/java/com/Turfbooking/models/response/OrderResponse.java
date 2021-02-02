package com.Turfbooking.models.response;

import com.Turfbooking.documents.Order;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OrderResponse {

    private String userId;
    private String orderId;
    private List<TimeSlotResponse> timeSlots;
    private LocalDateTime timestamp;

    public OrderResponse(Order order) {
        this.userId = order.getUserId();
        this.timestamp = order.getTimestamp();

    }

}
