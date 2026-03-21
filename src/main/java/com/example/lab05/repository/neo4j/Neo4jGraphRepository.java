package com.example.lab05.repository.neo4j;

import java.util.List;
import java.util.Map;

import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Repository;

// TODO (Section 5 — Neo4j):
//
// Pattern 3: Neo4jClient — programmatic Cypher execution.
// Use this when the query returns aggregated/projected data
// that does not map to a @Node entity (e.g., product names + friend counts).
//
// 1. Inject Neo4jClient via constructor injection
// 2. Implement getRecommendations(String personName, int limit):
//
//    return neo4jClient
//        .query("MATCH (p:Person {name: $name})" +
//               "-[:FOLLOWS]->(friend)" +
//               "-[:PURCHASED]->(prod) " +
//               "WHERE NOT (p)-[:PURCHASED]->(prod) " +
//               "RETURN prod.name AS product, " +
//               "       COUNT(friend) AS score " +
//               "ORDER BY score DESC LIMIT $limit")
//        .bind(personName).to("name")
//        .bind(limit).to("limit")
//        .fetch()
//        .all()
//        .stream().toList();

@Repository
public class Neo4jGraphRepository {

    // Inject Neo4jClient here
    
    private final Neo4jClient neo4jClient;
    public Neo4jGraphRepository(Neo4jClient neo4jClient) {
        this.neo4jClient = neo4jClient;
    }

    public List<Map<String, Object>> getRecommendations(String personName, int limit) {
        return neo4jClient
            .query("MATCH (p:Person {name: $name})" +
                   "-[:FOLLOWS]->(friend)" +
                   "-[:PURCHASED]->(prod) " +
                   "WHERE NOT (p)-[:PURCHASED]->(prod) " +
                   "RETURN prod.name AS product, " +
                   "       COUNT(friend) AS score " +
                   "ORDER BY score DESC LIMIT $limit")
            .bind(personName).to("name")
            .bind(limit).to("limit")
            .fetch()
            .all()
            .stream().toList();
    }
}
