package com.Turfbooking.documents;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@Builder
@Document
@ToString
public class OpenCloseTime {

    @Id
    private String _id;
    private String day;
    private LocalDate date;
    private LocalTime openTime;
    private LocalTime closeTime;
    private LocalDateTime timestamp;

}
