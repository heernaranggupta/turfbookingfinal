package com.Turfbooking.documents;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Document
public class StartEndTime {

    @Id
    private String _id;
    private String day;
    private LocalDate date;
    private String turfId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Double price;
    private LocalDateTime timestamp;

}
