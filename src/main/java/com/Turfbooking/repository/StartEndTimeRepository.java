package com.Turfbooking.repository;

import com.Turfbooking.documents.StartEndTime;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StartEndTimeRepository extends MongoRepository<StartEndTime, String> {

    @Query("{'date':{$eq: ?0}}")
    List<StartEndTime> findByDate(LocalDate date);

    @Query("{'day':{$eq: ?0}}")
    List<StartEndTime> findByDay(String day);

    @Query("{'date':{$gte: ?0}}")
    List<StartEndTime> deleteByDate(LocalDate date);

    @Query("{'day': ?0}")
    List<StartEndTime> deleteByDay(String day);


}
