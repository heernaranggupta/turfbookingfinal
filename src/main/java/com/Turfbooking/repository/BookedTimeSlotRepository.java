package com.Turfbooking.repository;

import com.Turfbooking.documents.BookedTimeSlot;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface BookedTimeSlotRepository extends MongoRepository<BookedTimeSlot, String> {

    @Query("{'slotNumber':?0 ,'date':{ $gte: ?1}}")
    BookedTimeSlot findByDateAndSlotNumber(Integer slotNumber, LocalDate date);

    @Query("{'date': { $gte: ?0}}")
    List<BookedTimeSlot> findByDate(LocalDate date);

    @Query("{'date' :  { $gte: ?1, $lte: ?2 }} ")
    List<BookedTimeSlot> findAllByFromDateAndToDate( LocalDate fromDate, LocalDate toDate);

    @Query("{'date' :  { $gte: ?0, $lte: ?1 } ,'status' :?2}")
    List<BookedTimeSlot> findAllByDateAndStatus( LocalDate fromDate, LocalDate toDate,String status);

    @Query("{'bookingId':?0}")
    BookedTimeSlot findByBookingId(String bookingId);

    @Query("{'userId': ?0}")
    List<BookedTimeSlot> findByUserId(String userId);

    @Query("{'status':?0 ,'date':{ $gte: ?1}}")
    List<BookedTimeSlot> findByDateAndBookingStatus(String status, LocalDate date);

}
