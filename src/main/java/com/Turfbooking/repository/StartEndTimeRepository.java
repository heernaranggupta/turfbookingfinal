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

    @Query("{'date':{$eq: ?0}}")
    List<StartEndTime> deleteByDate(LocalDate date);

    @Query("{'day': ?0}")
    List<StartEndTime> deleteByDay(String day);

    @Query("{'date':{'gt':startDate,'lt':endDate}}")
    List<StartEndTime> findByDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("{'turfId':turfId,{'date':{'gt':startDate,'lt':endDate}}}")
    List<StartEndTime> findByTurfIdEqualsAndDateBetween(String turfId, LocalDate startDate, LocalDate endDate);

}
