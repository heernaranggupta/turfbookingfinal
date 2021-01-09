package com.Turfbooking.repository;

import com.Turfbooking.documents.PaymentDetails;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentDetailsRepository extends MongoRepository<PaymentDetails,String> {
}
