package com.example.lab05.repository.mongo;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import com.example.lab05.dto.CategoryAvgDTO;

// TODO (Section 2 — MongoDB Aggregation):
//
// This is a standalone @Repository class that uses MongoTemplate
// for queries that go beyond what MongoRepository can auto-generate
// (e.g., aggregation pipelines).
//
// 1. Inject MongoTemplate via constructor injection
// 2. Implement getAverageByCategory() using an Aggregation pipeline:
//
//    Aggregation agg = Aggregation.newAggregation(
//        Aggregation.match(Criteria.where("rating").gt(4.0)),
//        Aggregation.group("category").avg("price").as("avgPrice"),
//        Aggregation.sort(Sort.Direction.DESC, "avgPrice"),
//        Aggregation.limit(5)
//    );
//
//    AggregationResults<CategoryAvgDTO> results =
//        mongoTemplate.aggregate(agg, "products", CategoryAvgDTO.class);
//
//    return results.getMappedResults();

@Repository
public class MongoAggregationRepository {

    // Inject MongoTemplate here
    private final MongoTemplate mongoTemplate;

    public MongoAggregationRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<CategoryAvgDTO> getAverageByCategory() {
        // Implement the aggregation pipeline described above
        Aggregation agg = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("rating").gt(4.0)),
            Aggregation.group("category").avg("price").as("avgPrice"),
            Aggregation.sort(Sort.Direction.DESC, "avgPrice"),
            Aggregation.limit(5)
        );
        AggregationResults<CategoryAvgDTO> results =
            mongoTemplate.aggregate(agg, "products", CategoryAvgDTO.class);
        return results.getMappedResults();
    }
}
