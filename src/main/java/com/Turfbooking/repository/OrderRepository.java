package com.Turfbooking.repository;

import com.Turfbooking.documents.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {

    @Query("{'userId':?0}")
    Order findByUserId(String userId);

}
