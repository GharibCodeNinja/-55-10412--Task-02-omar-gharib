package com.example.lab05.service;

import com.example.lab05.model.Product;
import com.example.lab05.model.cassandra.SensorReading;
import com.example.lab05.model.cassandra.SensorReadingKey;
import com.example.lab05.model.mongo.PurchaseReceipt;
import com.example.lab05.dto.PurchaseRequest;
import com.example.lab05.repository.PurchaseReceiptRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PurchaseService {

    private static final Logger log = LoggerFactory.getLogger(PurchaseService.class);

    private final ProductService productService;
    private final PurchaseReceiptRepository receiptRepository;
    private final SocialGraphService socialGraphService;
    private final SensorService sensorService;
    private final ProductSearchService searchService;
    private final RedisTemplate<String, Object> redisTemplate;

    public PurchaseService(ProductService productService, PurchaseReceiptRepository receiptRepository,
            SocialGraphService socialGraphService, SensorService sensorService,
            ProductSearchService searchService, RedisTemplate<String, Object> redisTemplate) {
        this.productService = productService;
        this.receiptRepository = receiptRepository;
        this.socialGraphService = socialGraphService;
        this.sensorService = sensorService;
        this.searchService = searchService;
        this.redisTemplate = redisTemplate;
    }

    public List<PurchaseReceipt> getReceiptsByPerson(String personName) {
        return receiptRepository.findByPersonName(personName);
    }

    public PurchaseReceipt executePurchase(PurchaseRequest request) {
        Product product = productService.getProductById(request.productId());
        if (product.getStockQuantity() < request.quantity())
            throw new RuntimeException("Insufficient stock for product: " + product.getName());
        product.setStockQuantity(product.getStockQuantity() - request.quantity());
        productService.updateProduct(product.getId(), product);

        PurchaseReceipt receipt = new PurchaseReceipt();
        receipt.setPersonName(request.personName());
        receipt.setProductName(product.getName());
        receipt.setProductCategory(product.getCategory());
        receipt.setQuantity(request.quantity());
        receipt.setUnitPrice(product.getPrice());
        receipt.setTotalPrice(product.getPrice() * request.quantity());
        receipt.setPurchaseDetails(request.purchaseDetails());
        receipt.setPurchasedAt(LocalDateTime.now());
        PurchaseReceipt saved = receiptRepository.save(receipt);

        try {
            socialGraphService.purchase(request.personName(), product.getName(), request.quantity(), product.getPrice());
        } catch (Exception e) {
            log.warn("Failed to create PURCHASED edge for {} -> {}: {}", request.personName(), product.getName(), e.getMessage());
        }

        try {
            SensorReading reading = new SensorReading();
            SensorReadingKey key = new SensorReadingKey("user-activity-" + request.personName().toLowerCase(), null);
            reading.setKey(key);
            reading.setLocation(product.getName());
            sensorService.recordReading(reading);
        } catch (Exception e) {
            log.warn("Failed to log purchase event for {}: {}", request.personName(), e.getMessage());
        }

        try {
            if (product.getStockQuantity() == 0) {
                var results = searchService.searchByName(product.getName());
                if (!results.isEmpty()) {
                    var esProduct = results.get(0);
                    esProduct.setInStock(false);
                    searchService.saveProduct(esProduct);
                }
            }
        } catch (Exception e) {
            log.warn("Failed to update ES inStock for product {}: {}", product.getId(), e.getMessage());
        }

        try {
            redisTemplate.delete("dashboard:" + request.personName());
        } catch (Exception e) {
            log.warn("Failed to evict dashboard cache for {}: {}", request.personName(), e.getMessage());
        }

        return saved;
    }
}
