package com.example.lab05.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Assembles data from all six databases into a single response.
 * Every field comes from a different database.
 */
public record SmartProductPage(
    String name,                        // PostgreSQL
    Double price,                       // PostgreSQL
    Integer stock,                      // PostgreSQL
    Map<String, Object> specifications, // MongoDB
    List<String> tags,                  // MongoDB
    List<String> relatedProducts,       // Elasticsearch
    List<String> friendsWhoBought,      // Neo4j
    boolean servedFromCache             // Redis
) implements Serializable {}