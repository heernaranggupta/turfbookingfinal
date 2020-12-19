package com.Turfbooking.repository;

import com.Turfbooking.documents.OpenCloseTime;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OpenCloseTimeRepository extends MongoRepository<OpenCloseTime ,String> {


}
