package com.Turfbooking.documents;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@Document
@Data
public class BookedTimeSlot {

    @Id
    private String _id;
    private String bookingId;
    private String userId;//phoneNumber or unique id
    private Integer slotNumber;
    private String turfId; //turfId
    private String status;
    private LocalDate date;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime timeStamp;//add timestamp

}
