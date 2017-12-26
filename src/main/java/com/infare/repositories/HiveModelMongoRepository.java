package com.infare.repositories;

import com.infare.domain.HiveModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface HiveModelMongoRepository extends MongoRepository<HiveModel, Long> { 
}
