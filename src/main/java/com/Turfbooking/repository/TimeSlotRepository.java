package com.Turfbooking.repository;

import com.Turfbooking.documents.BookedTimeSlot;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TimeSlotRepository  extends MongoRepository<BookedTimeSlot,String> {

    @Query("{'slotNumber':?0 ,'date':{ $gte: ?1}}")
    BookedTimeSlot findByDateAndSlotNumber(Integer slotNumber, LocalDate date);

    @Query("{'date': { $gte: ?0}}")
    List<BookedTimeSlot> findByDate(LocalDate date);

}