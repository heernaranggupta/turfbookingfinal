package com.Turfbooking.repository;

import com.Turfbooking.documents.Cart;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends MongoRepository<Cart, String> {

    Cart findByUserPhoneNumber(String phoneNumber);

}
