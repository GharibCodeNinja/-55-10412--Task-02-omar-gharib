package com.example.lab05.repository.mongo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.lab05.model.mongo.MongoProduct;

// TODO (Section 2 — MongoDB):
// 1. Make this interface extend MongoRepository<MongoProduct, String>
// 2. Add the derived query methods and @Query methods:
//    - List<MongoProduct> findByCategory(String category)
//    - List<MongoProduct> findByPriceBetween(Double min, Double max)
//    - @Query("{ 'tags': ?0 }") List<MongoProduct> findByTag(String tag)

@Repository
public interface MongoProductRepository extends MongoRepository<MongoProduct, String> {
    List<MongoProduct> findByCategory(String category);
    List<MongoProduct> findByPriceBetween(Double min, Double max);
    
    @Query("{ 'tags': ?0 }")
    List<MongoProduct> findByTag(String tag);
}
