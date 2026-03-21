package com.example.lab05.model.elastic;

import java.util.List;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import jakarta.persistence.Id;

// TODO (Section 6 — Elasticsearch):
// Add the following annotations as described in the manual:
//
// @Document(indexName = "products")
// public class ProductDocument {
//
//     @Id
//     private String id;
//
//     @Field(type = FieldType.Text, analyzer = "english")
//     private String name;
//
//     @Field(type = FieldType.Text, analyzer = "english")
//     private String description;
//
//     @Field(type = FieldType.Keyword)
//     private String category;
//
//     @Field(type = FieldType.Double)
//     private Double price;
//
//     @Field(type = FieldType.Keyword)
//     private List<String> tags;
//
//     @Field(type = FieldType.Boolean)
//     private Boolean inStock;
// }

@Document(indexName = "products")
public class ProductDocument {

    @Id
    private String id;
    @Field(type = FieldType.Text, analyzer = "english")
    private String name;
    @Field(type = FieldType.Text, analyzer = "english")
    private String description;
    @Field(type = FieldType.Keyword)
    private String category;
    @Field(type = FieldType.Double)
    private Double price;
    @Field(type = FieldType.Keyword)
    private List<String> tags;
    @Field(type = FieldType.Boolean)
    private Boolean inStock;

    public ProductDocument() {}

    // ── Getters & Setters ──

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public Boolean getInStock() { return inStock; }
    public void setInStock(Boolean inStock) { this.inStock = inStock; }
}
