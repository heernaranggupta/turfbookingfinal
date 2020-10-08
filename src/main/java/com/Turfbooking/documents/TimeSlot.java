package com.Turfbooking.documents;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@Document
public class TimeSlot {

    @Id
    private String _id;
    private String userId;//phoneNumber or unique id
    private Integer slotNumber;
    private String companyId;
    private LocalDate date;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

}
