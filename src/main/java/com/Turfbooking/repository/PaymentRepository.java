package com.Turfbooking.repository;

import com.Turfbooking.documents.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {

    @Query("{'id':?0}")
    Payment findByPaymentId(String id);

}
