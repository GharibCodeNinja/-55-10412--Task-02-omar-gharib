package com.example.lab05.service;

import com.example.lab05.dto.DashboardResponse;
import com.example.lab05.model.cassandra.SensorReading;
import com.example.lab05.model.mongo.PurchaseReceipt;
import com.example.lab05.repository.PurchaseReceiptRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private static final Logger log = LoggerFactory.getLogger(DashboardService.class);

    private final PurchaseReceiptRepository receiptRepository;
    private final SocialGraphService socialGraphService;
    private final SensorService sensorService;
    private final ProductSearchService searchService;
    private final RedisTemplate<String, Object> redisTemplate;

    public DashboardService(PurchaseReceiptRepository receiptRepository, SocialGraphService socialGraphService,
            SensorService sensorService, ProductSearchService searchService,
            RedisTemplate<String, Object> redisTemplate) {
        this.receiptRepository = receiptRepository;
        this.socialGraphService = socialGraphService;
        this.sensorService = sensorService;
        this.searchService = searchService;
        this.redisTemplate = redisTemplate;
    }

    public DashboardResponse getDashboard(String personName) {
        String cacheKey = "dashboard:" + personName;

        // Step 0 - Redis cache check
        try {
            Object cached = redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                DashboardResponse r = (DashboardResponse) cached;
                return new DashboardResponse(r.personName(), r.totalSpent(), r.purchaseCount(),
                        r.recentPurchases(), r.friendRecommendations(), r.friendsOfFriends(),
                        r.recentActivity(), r.youMightAlsoLike(), true);
            }
        } catch (Exception e) {
            log.warn("Redis cache check failed for {}: {}", personName, e.getMessage());
        }

        // Step 1 - MongoDB
        List<PurchaseReceipt> allReceipts = receiptRepository.findByPersonName(personName);
        double totalSpent = allReceipts.stream().mapToDouble(PurchaseReceipt::getTotalPrice).sum();
        int purchaseCount = allReceipts.size();
        List<PurchaseReceipt> recentPurchases = allReceipts.stream()
                .skip(Math.max(0, allReceipts.size() - 5)).collect(Collectors.toList());

        // Step 2 - Neo4j
        List<Map<String, Object>> friendRecs = new ArrayList<>();
        List<String> friendsOfFriends = new ArrayList<>();
        try {
            friendRecs = socialGraphService.getRecommendations(personName, 5);
            friendsOfFriends = socialGraphService.getFriendsOfFriends(personName)
                    .stream().map(p -> p.getName()).collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("Failed to fetch Neo4j data for {}: {}", personName, e.getMessage());
        }

        // Step 3 - Cassandra
        List<SensorReading> recentActivity = new ArrayList<>();
        try {
            recentActivity = sensorService.getLatestReadings("user-activity-" + personName.toLowerCase(), 10);
        } catch (Exception e) {
            log.warn("Failed to fetch activity for {}: {}", personName, e.getMessage());
        }

        // Step 4 - Elasticsearch
        List<String> youMightAlsoLike = new ArrayList<>();
        try {
            Set<String> purchased = allReceipts.stream()
                    .map(PurchaseReceipt::getProductName).collect(Collectors.toSet());
            Set<String> categories = allReceipts.stream()
                    .map(PurchaseReceipt::getProductCategory).collect(Collectors.toSet());
            for (String cat : categories) {
                searchService.getByCategory(cat).stream()
                        .filter(p -> !purchased.contains(p.getName()))
                        .limit(2)
                        .map(p -> p.getName())
                        .forEach(youMightAlsoLike::add);
            }
        } catch (Exception e) {
            log.warn("Failed to fetch ES suggestions for {}: {}", personName, e.getMessage());
        }

        // Step 5 - Build and cache
        DashboardResponse response = new DashboardResponse(personName, totalSpent, purchaseCount,
                recentPurchases, friendRecs, friendsOfFriends, recentActivity, youMightAlsoLike, false);
        try {
            redisTemplate.opsForValue().set(cacheKey, response, 5, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.warn("Failed to cache dashboard for {}: {}", personName, e.getMessage());
        }
        return response;
    }
}
