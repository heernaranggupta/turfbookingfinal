package com.Turfbooking.repository;

import com.Turfbooking.documents.BookedTimeSlot;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookedTimeSlotRepository extends MongoRepository<BookedTimeSlot, String> {

    @Query("{'turfId':?0,'startTime':?1}")
    BookedTimeSlot findByTurfIdAndStartTime(String turfId, LocalDateTime startTime);

    @Query("{'date':{ $eq: ?0}, 'turfId':?1}")
    List<BookedTimeSlot> findByDateAndTurfId(LocalDate date, String turfId);

    @Query("{'date': { $eq: ?0}}")
    List<BookedTimeSlot> findByDate(LocalDate date);

    @Query("{'date' :  { $gte: ?0, $lte: ?1 }} ")
    List<BookedTimeSlot> findAllByDate(LocalDate fromDate, LocalDate toDate);

    @Query("{'date' :  { $gte: ?0, $lte: ?1 } ,'status' :?2}")
    List<BookedTimeSlot> findAllByDateAndStatus(LocalDate fromDate, LocalDate toDate, String status);

    @Query("{'bookingId':?0}")
    BookedTimeSlot findByBookingId(String bookingId);

    @Query("{'userId': ?0}")
    List<BookedTimeSlot> findByUserId(String userId);

    @Query("{'status':?0 ,'date':{ $gte: ?1}}")
    List<BookedTimeSlot> findByDateAndBookingStatus(String status, LocalDate date);

}
