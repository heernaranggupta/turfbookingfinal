package com.Turfbooking.documents;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@Document
public class Order {

    @Id
    private String _id;
    @Indexed
    private String userId;
    private String paymentId;
    private String paymentMethod;
    private List<String> timeSlots; //list of booking ids of slots
    private LocalDateTime timestamp;

}
