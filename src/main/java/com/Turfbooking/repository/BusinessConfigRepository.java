package com.Turfbooking.repository;

import com.Turfbooking.documents.Business;
import com.Turfbooking.documents.BusinessConfig;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface BusinessConfigRepository extends MongoRepository<BusinessConfig, String> {

    BusinessConfig findByDate(LocalDate date);

    Business findByDay(String day);
}
