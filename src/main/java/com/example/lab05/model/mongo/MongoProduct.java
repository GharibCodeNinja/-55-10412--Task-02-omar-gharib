package com.example.lab05.model.mongo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

// TODO (Section 2 — MongoDB):
// Add the following annotations and fields as described in the manual:
//
// @Document(collection = "products")
// public class MongoProduct {
//
//     @Id
//     private String id;
//
//     @Indexed
//     private String name;
//
//     private String category;
//     private Double price;
//     private Double rating;
//
//     private List<String> tags = new ArrayList<>();
//     private Map<String, Object> specifications = new HashMap<>();
//
//     // Add getters, setters, and constructors
// }
@Document(collection = "products")
public class MongoProduct {

    @Id
    private String id;
    @Indexed
    private String name;
    private String category;
    private Double price;
    private Double rating;
    private List<String> tags = new ArrayList<>();
    private Map<String, Object> specifications = new HashMap<>();

    public MongoProduct() {}

    // ── Getters & Setters ──

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public Map<String, Object> getSpecifications() { return specifications; }
    public void setSpecifications(Map<String, Object> specifications) { this.specifications = specifications; }
}
