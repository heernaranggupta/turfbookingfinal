package com.Turfbooking.documents;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Document
public class OpenCloseTime {

    @Id
    private String _id;
    private String day;
    private LocalDate date;
    private LocalDateTime openTime;
    private LocalDateTime closeTime;
    private LocalDateTime timestamp;

}
