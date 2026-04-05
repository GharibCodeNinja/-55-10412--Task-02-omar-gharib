package com.example.lab05.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.lab05.dto.CategoryAvgDTO;
import com.example.lab05.model.mongo.MongoProduct;
import com.example.lab05.repository.mongo.MongoAggregationRepository;
import com.example.lab05.repository.mongo.MongoProductRepository;

@Service
public class MongoProductService {

    // TODO: Inject MongoProductRepository via constructor injection
    // TODO: Inject MongoAggregationRepository via constructor injection
    private final MongoProductRepository mongoProductRepository;
    private final MongoAggregationRepository mongoAggregationRepository;

    public MongoProductService(MongoProductRepository mongoProductRepository, MongoAggregationRepository mongoAggregationRepository) {
        this.mongoProductRepository = mongoProductRepository;
        this.mongoAggregationRepository = mongoAggregationRepository;
    }

    // TODO: Implement getAllProducts()
    //   - Return all MongoProduct documents
    public List<MongoProduct> getAllProducts() {
        return mongoProductRepository.findAll();
    }

    // TODO: Implement getProductById(String id)
    //   - Return the product or throw RuntimeException("Product not found: " + id)
    public MongoProduct getProductById(String id) {
        return mongoProductRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found: " + id));
    }

    public MongoProduct createProduct(MongoProduct product) {
        return mongoProductRepository.save(product);
    }

    // TODO: Implement deleteProduct(String id)
    //   - Delete the product by id
    public void deleteProduct(String id) {
        mongoProductRepository.deleteById(id);
    }

    // TODO: Implement getByCategory(String category)
    //   - Delegate to MongoProductRepository
    public List<MongoProduct> getByCategory(String category) {
        return mongoProductRepository.findByCategory(category);
    }

    // TODO: Implement getByPriceRange(Double min, Double max)
    //   - Delegate to MongoProductRepository
    public List<MongoProduct> getByPriceRange(Double min, Double max) {
        return mongoProductRepository.findByPriceBetween(min, max);
    }

    // TODO: Implement getByTag(String tag)
    //   - Delegate to MongoProductRepository
    public List<MongoProduct> getByTag(String tag) {
        return mongoProductRepository.findByTag(tag);
    }

    // TODO: Implement getAverageByCategory()
    //   - Delegate to MongoAggregationRepository
    public List<CategoryAvgDTO> getAverageByCategory() {
        return mongoAggregationRepository.getAverageByCategory();
    }
}
