package com.Turfbooking.repository;

import com.Turfbooking.documents.CancelledSlot;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CancelledSlotRepository extends MongoRepository<CancelledSlot, String> {

    @Query("{'userId':?0}")
    List<CancelledSlot> findByUserId(String userId);

    @Query("{'date' :  { $gte: ?0, $lte: ?1 }}")
    List<CancelledSlot> findAllByDate(LocalDate fromDate, LocalDate toDate);

}
