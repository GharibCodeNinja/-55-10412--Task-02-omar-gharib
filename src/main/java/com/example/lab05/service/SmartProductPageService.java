package com.example.lab05.service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.lab05.dto.SmartProductPage;
import com.example.lab05.model.Product;
import com.example.lab05.model.cassandra.SensorReading;
import com.example.lab05.model.cassandra.SensorReadingKey;
import com.example.lab05.model.elastic.ProductDocument;
import com.example.lab05.model.mongo.MongoProduct;

// TODO (Section 7 — Polyglot Integration):
//
// Inject all the services you built in previous sections:
//   - ProductService         (PostgreSQL)
//   - MongoProductService    (MongoDB)
//   - ProductSearchService   (Elasticsearch)
//   - SocialGraphService     (Neo4j)
//   - SensorService          (Cassandra)
//   - RedisTemplate<String, Object>  (Redis — for caching the assembled page)
//
// Constructor injection for all six.

// TODO: Implement getSmartPage(Long productId, String userName)
//
// Build a SmartProductPage by assembling data from all databases.
// Key design pattern: GRACEFUL DEGRADATION
//   - PostgreSQL has NO try-catch -- it's the hard dependency
//   - Every other database is wrapped in try-catch
//   - If Neo4j is down, the page still loads (just no friend recs)
//
// Steps:
//
// 0. Redis -- CHECK CACHE FIRST
//    String cacheKey = "smart-page:" + productId + ":" + userName;
//    Object cached = redisTemplate.opsForValue().get(cacheKey);
//    if (cached != null) {
//        SmartProductPage page = (SmartProductPage) cached;
//        page.setServedFromCache(true);
//        return page;
//    }
//
// 1. PostgreSQL (HARD dependency -- no try-catch)
//    Product product = pgService.getProductById(productId);
//    page.setName(product.getName());
//    page.setPrice(product.getPrice());
//    page.setStock(product.getStockQuantity());
//
// 2. MongoDB (try-catch)
//    Get products by category, take first result's specs/tags
//
// 3. Elasticsearch (try-catch)
//    Search by product name, take top 3 related product names
//
// 4. Neo4j (try-catch)
//    Get recommendations for userName, extract product names
//
// 5. Cassandra (try-catch -- fire and forget)
//    Log a page view event
//
// 6. Redis -- SAVE ASSEMBLED PAGE TO CACHE
//    redisTemplate.opsForValue().set(cacheKey, page, Duration.ofMinutes(2));
//    page.setServedFromCache(false);
//
// Return the assembled SmartProductPage
@Service
public class SmartProductPageService {

    private static final Logger log = LoggerFactory.getLogger(SmartProductPageService.class);

    // In a real implementation you would inject:
    // - RedisTemplate or RedisCacheManager
    // - MongoProductService
    // - ProductService (PostgreSQL)
    // - ProductSearchService (Elasticsearch)
    // - SocialGraphService (Neo4j)
    // - SensorService or an EventService (Cassandra)
    private final RedisTemplate<String,SmartProductPage> redisTemplate;
    private final MongoProductService mongoProductService;
    private final ProductService productService;
    private final ProductSearchService searchService;
    private final SocialGraphService socialGraphService;
    private final SensorService sensorService;

    public SmartProductPageService(RedisTemplate<String, SmartProductPage> redisTemplate,
            MongoProductService mongoProductService,
            ProductService productService,
            ProductSearchService searchService,
            SocialGraphService socialGraphService,
            SensorService sensorService) {
        this.redisTemplate = redisTemplate;
        this.mongoProductService = mongoProductService;
        this.productService = productService;
        this.searchService = searchService;
        this.socialGraphService = socialGraphService;
        this.sensorService = sensorService;
    }

    /**
     * Pseudo-implementation showing the orchestration pattern.
     * Each step is wrapped in try-catch for graceful degradation.
     */
    public SmartProductPage getSmartPage(Long productId, String userName) {
        log.info("Assembling smart product page for product={}, user={}", productId, userName);

        String cacheKey = "smart-page:" + productId + ":" + userName;
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            SmartProductPage page = (SmartProductPage) cached;
            page.setServedFromCache(true);
            return page;
        }

        SmartProductPage page = new SmartProductPage();

        // 1. PostgreSQL (HARD dependency -- no try-catch)
        Product product = productService.getProductById(productId);
        page.setName(product.getName());
        page.setPrice(product.getPrice());
        page.setStock(product.getStockQuantity());
        // 2. MongoDB -- flexible specs (soft dependency)
        try {
            List<MongoProduct> mongoResults = mongoProductService.getByCategory(product.getCategory());
            if (!mongoResults.isEmpty()) {
                MongoProduct mp = mongoResults.get(0);
                page.setSpecifications(mp.getSpecifications());
                page.setTags(mp.getTags());
            }
        } catch (Exception e) {
            // MongoDB down -- page still works
        }

        // 3. Elasticsearch -- related products (soft)
        try {
            List<ProductDocument> related = searchService.searchByName(product.getName());
            page.setRelatedProducts(related.stream()
                    .map(ProductDocument::getName)
                    .limit(3).toList());
        } catch (Exception e) {
            // ES down -- no related products
        }

        // 4. Neo4j -- friends who bought (soft)
        try {
            List<Map<String, Object>> recs = socialGraphService.getRecommendations(userName, 3);
            page.setFriendsWhoBought(recs.stream()
                    .map(r -> (String) r.get("product"))
                    .toList());
        } catch (Exception e) {
            // Neo4j down -- no social proof
        }

        // 5. Cassandra -- log the view event (fire and forget)
        try {
            SensorReading event = new SensorReading();
            SensorReadingKey key = new SensorReadingKey();
            key.setSensorId("page-view-" + productId);
            key.setReadingTime(Instant.now());
            event.setKey(key);
            event.setTemperature(0.0);
            event.setHumidity(0.0);
            event.setLocation("user:" + userName);
            sensorService.recordReading(event);
        } catch (Exception e) {
            // Cassandra down -- view not logged
        }

        // 6. Redis -- SAVE TO CACHE (TTL = 2 minutes)
        redisTemplate.opsForValue().set(cacheKey, page, Duration.ofMinutes(2));
        page.setServedFromCache(false);
        // Return the assembled SmartProductPage
        return page;
    }
}
