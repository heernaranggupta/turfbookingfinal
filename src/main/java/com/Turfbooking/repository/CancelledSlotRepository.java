package com.Turfbooking.repository;

import com.Turfbooking.documents.CancelledSlot;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface CancelledSlotRepository extends MongoRepository<CancelledSlot, String> {

    @Query("{'userId':?0}")
    List<CancelledSlot> findByUserId(String userId);

}
