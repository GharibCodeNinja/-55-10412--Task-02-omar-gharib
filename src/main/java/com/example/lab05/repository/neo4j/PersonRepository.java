package com.example.lab05.repository.neo4j;

import java.util.List;
import java.util.Optional;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import com.example.lab05.model.neo4j.Person;

// TODO (Section 5 — Neo4j):
// 1. Make this interface extend Neo4jRepository<Person, Long>
// 2. Add the following methods:
//
//    // Pattern 1: Derived query (auto-generated)
//    Optional<Person> findByName(String name);
//
//    // Pattern 2: @Query with Cypher (friends of friends)
//    @Query("MATCH (p:Person {name: $name})" +
//           "-[:FOLLOWS*2]-(fof:Person) " +
//           "WHERE fof <> p " +
//           "AND NOT (p)-[:FOLLOWS]-(fof) " +
//           "RETURN DISTINCT fof")
//    List<Person> findFriendsOfFriends(@Param("name") String name);
//
// Pattern 3 (Neo4jClient) lives in Neo4jGraphRepository.

public interface PersonRepository extends Neo4jRepository<Person, Long> {
    // Replace this with the Neo4jRepository extension and query methods
    Optional<Person> findByName(String name);
    
    @Query("MATCH (p:Person {name: $name})" +
           "-[:FOLLOWS*2]-(fof:Person) " +
           "WHERE fof <> p " +
           "AND NOT (p)-[:FOLLOWS]-(fof) " +
           "RETURN DISTINCT fof")
    List<Person> findFriendsOfFriends(String name);
}
