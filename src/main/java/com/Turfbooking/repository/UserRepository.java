package com.Turfbooking.repository;

import com.Turfbooking.documents.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User,String> {

    User findByPhoneNumber(String phoneNumber);
    User findByEmailId(String emailId);

   /* @Query("{'emaiId' : ?0}")
    User findByEmail(String email);*/

    @Query("{'phoneNumber' : ?0 , 'password' : ?1}")
    User findByPhoneNumberAndPassword(String phoneNumber, String password);

    @Query("{'emailId' : ?0 , 'password' : ?1}")
    User findByEmailIdAndPassword(String phoneNumber, String password);



}
