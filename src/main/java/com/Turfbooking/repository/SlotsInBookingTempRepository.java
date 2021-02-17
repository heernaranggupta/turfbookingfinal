package com.Turfbooking.repository;

import com.Turfbooking.documents.SlotsInBookingTemp;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SlotsInBookingTempRepository extends MongoRepository<SlotsInBookingTemp, String> {

    @Query("{'turfId':?0,'startTime':?1,'date':{$eq :?2}}")
    SlotsInBookingTemp findByTurfIdAndStartTimeAndDate(String turfId, LocalDateTime startTime, LocalDate date);

    @Transactional
    @Query("{'turfId':?0,'startTime':?1,'date':{$eq :?2}}")
    SlotsInBookingTemp deleteByTurfIdAndStartTimeAndDate(String turfId, LocalDateTime startTime, LocalDate date);

    @Query("{'timestamp':{$lt :?1}}")
    List<SlotsInBookingTemp> deleteByTimestamp(LocalDateTime time);

}
