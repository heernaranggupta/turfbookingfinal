package com.Turfbooking.repository;

import com.Turfbooking.documents.Otp;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OtpRepository extends MongoRepository<Otp,String> {

    @Query("{'phoneNumber' : ?0 , 'otp' : ?1}")
    Otp findByPhoneNumberAndOtp(String phoneNumber, Integer otp);

    Otp findByPhoneNumber(String phoneNumber);

    List<Otp> findAll();

    @Query(value = "{'otpStatusActive' : ?0}", delete = true)
    long deleteByOtpStatusActive(String otpStatusActive);

    long deleteByPhoneNumber(String phoneNumber);

    @Query(value = "{'otpStatusActive' : ?0}")
    long findByOtpStatusActive(String otpStatusActive);
}
