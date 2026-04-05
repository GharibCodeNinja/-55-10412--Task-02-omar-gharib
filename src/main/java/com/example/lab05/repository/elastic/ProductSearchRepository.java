package com.example.lab05.repository.elastic;

import java.util.List;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import com.example.lab05.model.elastic.ProductDocument;

// TODO (Section 6 — Elasticsearch):
// 1. Make this interface extend ElasticsearchRepository<ProductDocument, String>
// 2. Add the following methods:
//
//    // Pattern 1: Derived query (auto-generated, works on Keyword/numeric fields)
//    List<ProductDocument> findByCategory(String category);
//    List<ProductDocument> findByPriceBetween(Double min, Double max);
//
//    // Pattern 2: @Query with Elasticsearch JSON (fuzzy match on Text field)
//    @Query("{\"match\": {\"name\": {\"query\": \"?0\", \"fuzziness\": \"AUTO\"}}}")
//    List<ProductDocument> searchByName(String name);
//
// Pattern 3 (ElasticsearchOperations) lives in ElasticSearchQueryRepository.

@Repository
public interface ProductSearchRepository extends ElasticsearchRepository<ProductDocument, String> {
    // Pattern 1: Derived query (auto-generated, works on Keyword/numeric fields)
    List<ProductDocument> findByCategory(String category);
    List<ProductDocument> findByPriceBetween(Double min, Double max);

    // Pattern 2: @Query with Elasticsearch JSON (fuzzy match on Text field)
    @Query("{\"match\": {\"name\": {\"query\": \"?0\", \"fuzziness\": \"AUTO\"}}}")
    List<ProductDocument> searchByName(String name);
}
