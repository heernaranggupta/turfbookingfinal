package com.Turfbooking.repository;

import com.Turfbooking.documents.Cart;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CartRepository extends MongoRepository<Cart, String> {

    Cart findByUserPhoneNumber(String phoneNumber);

    Cart findBy_cartId(String cartId);

    @Query("DELETE FROM cart AS c WHERE c.userPhoneNumber = null AND  timeStamp > :date")
    List<Cart> deleteNonUsedCarts(@Param("date")LocalDateTime dateTime);

}
