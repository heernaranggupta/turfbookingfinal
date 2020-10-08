package com.Turfbooking.repository;

import com.Turfbooking.documents.Business;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessRepository extends MongoRepository<Business, String> {

    @Query("{'username' : ?0 , 'password' : ?1}")
    Business findByUsernameAndPassword(String username, String password);

    //@Query("{'phoneNumber' : ?0 ,'password': ?1'}")
    Business findByPhoneNumber(String phoneNumber);

}
