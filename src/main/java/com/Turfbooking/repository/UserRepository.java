package com.Turfbooking.repository;

import com.Turfbooking.documents.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    @Query("{'phoneNumber':?0}")
    User findByPhoneNumber(String phoneNumber);

    @Query("{'phoneNumber':?0, 'emailId' : ?1}")
    User findByPhoneNumberOrEmailId(String phoneNumber, String email);

    User findByEmailId(String emailId);

   /* @Query("{'emaiId' : ?0}")
    User findByEmail(String email);*/

    @Query("{'phoneNumber' : ?0 , 'password' : ?1}")
    User findByPhoneNumberAndPassword(String phoneNumber, String password);

    @Query("{'emailId' : ?0 , 'password' : ?1}")
    User findByEmailIdAndPassword(String phoneNumber, String password);

    @Query("{'role':?0}")
    List<User> findByRole(String role);

}
