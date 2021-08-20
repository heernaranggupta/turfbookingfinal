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

    @Query("{'date':{'$gt':?0,'$lt':?1}}")
    List<StartEndTime> findByDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("{'turfId':?0,{'date':{'$gt':?1,'$lt':?2}}}")
    List<StartEndTime> findByTurfIdEqualsAndDateBetween(String turfId, LocalDate startDate, LocalDate endDate);

    @Query("{'turfId':?0,'date':{'$eq':?1}}")
    List<StartEndTime> findByTurfIdAndDate(String turfId, LocalDate date);

}
