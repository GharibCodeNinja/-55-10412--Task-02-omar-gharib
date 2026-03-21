package com.example.lab05.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Assembles data from all six databases into a single response.
 * Every field comes from a different database.
 */
public class SmartProductPage implements Serializable {

    // From PostgreSQL -- source of truth
    private String name;
    private Double price;
    private Integer stock;

    // From MongoDB -- flexible product attributes
    private Map<String, Object> specifications;
    private List<String> tags;

    // From Elasticsearch -- related products
    private List<String> relatedProducts;

    // From Neo4j -- social proof
    private List<String> friendsWhoBought;

    // From Redis -- was this served from cache?
    private boolean servedFromCache;

    public SmartProductPage() {}

    // ── Getters & Setters ──

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public Map<String, Object> getSpecifications() { return specifications; }
    public void setSpecifications(Map<String, Object> specifications) { this.specifications = specifications; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public List<String> getRelatedProducts() { return relatedProducts; }
    public void setRelatedProducts(List<String> relatedProducts) { this.relatedProducts = relatedProducts; }

    public List<String> getFriendsWhoBought() { return friendsWhoBought; }
    public void setFriendsWhoBought(List<String> friendsWhoBought) { this.friendsWhoBought = friendsWhoBought; }

    public boolean isServedFromCache() { return servedFromCache; }
    public void setServedFromCache(boolean servedFromCache) { this.servedFromCache = servedFromCache; }
}
