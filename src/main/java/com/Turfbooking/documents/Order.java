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
    private List<String> timeSlots;
    private LocalDateTime timestamp;

}
