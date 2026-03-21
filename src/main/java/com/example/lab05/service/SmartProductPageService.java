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

@Service
public class SmartProductPageService {

    private static final Logger log = LoggerFactory.getLogger(SmartProductPageService.class);

    private final RedisTemplate<String, SmartProductPage> redisTemplate;
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

    public SmartProductPage getSmartPage(Long productId, String userName) {
        log.info("Assembling smart product page for product={}, user={}", productId, userName);

        // 0. Redis -- CHECK CACHE FIRST
        String cacheKey = "smart-page:" + productId + ":" + userName;
        SmartProductPage cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            // Record is immutable -- reconstruct with servedFromCache = true
            return new SmartProductPage(
                cached.name(), cached.price(), cached.stock(),
                cached.specifications(), cached.tags(),
                cached.relatedProducts(), cached.friendsWhoBought(),
                true
            );
        }

        // Collect into local variables (records are immutable -- no setters)
        // Then construct the record at the end.

        // 1. PostgreSQL (HARD dependency -- no try-catch)
        Product product = productService.getProductById(productId);
        String name = product.getName();
        Double price = product.getPrice();
        Integer stock = product.getStockQuantity();

        // 2. MongoDB -- flexible specs (soft dependency)
        Map<String, Object> specifications = null;
        List<String> tags = null;
        try {
            List<MongoProduct> mongoResults = mongoProductService.getByCategory(product.getCategory());
            if (!mongoResults.isEmpty()) {
                MongoProduct mp = mongoResults.get(0);
                specifications = mp.getSpecifications();
                tags = mp.getTags();
            }
        } catch (Exception e) {
            // MongoDB down -- page still works
        }

        // 3. Elasticsearch -- related products (soft)
        List<String> relatedProducts = null;
        try {
            List<ProductDocument> related = searchService.searchByName(product.getName());
            relatedProducts = related.stream()
                    .map(ProductDocument::getName)
                    .limit(3).toList();
        } catch (Exception e) {
            // ES down -- no related products
        }

        // 4. Neo4j -- friends who bought (soft)
        List<String> friendsWhoBought = null;
        try {
            List<Map<String, Object>> recs = socialGraphService.getRecommendations(userName, 3);
            friendsWhoBought = recs.stream()
                    .map(r -> (String) r.get("product"))
                    .toList();
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

        // 6. Construct the record and cache it
        SmartProductPage page = new SmartProductPage(
            name, price, stock,
            specifications, tags,
            relatedProducts, friendsWhoBought,
            false  // freshly assembled, not from cache
        );

        redisTemplate.opsForValue().set(cacheKey, page, Duration.ofMinutes(2));

        return page;
    }
}