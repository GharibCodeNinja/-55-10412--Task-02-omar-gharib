package com.example.lab05.model.neo4j;

import java.time.LocalDateTime;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

// TODO (Section 5 — Neo4j):
// Add the following annotations as described in the manual:
//
// @RelationshipProperties
// public class Purchased {
//
//     @Id @GeneratedValue
//     private Long id;
//
//     @TargetNode
//     private Neo4jProduct product;
//
//     private LocalDateTime purchasedAt;
//     private Integer quantity;
// }

@RelationshipProperties
public class Purchased {

    @Id
    @GeneratedValue
    private Long id;
    @TargetNode
    private Neo4jProduct product;
    private LocalDateTime purchasedAt;
    private Integer quantity;

    public Purchased() {}

    public Purchased(Neo4jProduct product, Integer quantity) {
        this.product = product;
        this.purchasedAt = LocalDateTime.now();
        this.quantity = quantity;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Neo4jProduct getProduct() { return product; }
    public void setProduct(Neo4jProduct product) { this.product = product; }

    public LocalDateTime getPurchasedAt() { return purchasedAt; }
    public void setPurchasedAt(LocalDateTime purchasedAt) { this.purchasedAt = purchasedAt; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}
