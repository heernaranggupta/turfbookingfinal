package com.Turfbooking.repository;

import com.Turfbooking.documents.OpenCloseTime;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface OpenCloseTimeRepository extends MongoRepository<OpenCloseTime ,String> {

    @Query("{'day':?0}")
    OpenCloseTime findByDay(String day);

    @Query("{'date':?0}")
    OpenCloseTime findByDate(LocalDate date);

}
