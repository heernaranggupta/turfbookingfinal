package com.Turfbooking.repository;

import com.Turfbooking.documents.PaymentDetails;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends MongoRepository<PaymentDetails, String> {

    @Query("{'orderId':?0}")
    PaymentDetails findByOrderId(String id);

}
