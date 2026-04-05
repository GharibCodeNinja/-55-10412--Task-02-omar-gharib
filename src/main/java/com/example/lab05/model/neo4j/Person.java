package com.example.lab05.model.neo4j;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;


// TODO (Section 5 — Neo4j):
// Add the following annotations as described in the manual:
//
// @Node
// public class Person {
//
//     @Id @GeneratedValue
//     private Long id;
//
//     private String name;
//
//     @Relationship(type = "FOLLOWS", direction = Relationship.Direction.OUTGOING)
//     private List<Person> following = new ArrayList<>();
//
//     @Relationship(type = "PURCHASED", direction = Relationship.Direction.OUTGOING)
//     private List<Purchased> purchases = new ArrayList<>();
// }

@Node
public class Person {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    @Relationship(type = "FOLLOWS", direction = Relationship.Direction.OUTGOING)
    private List<Person> following = new ArrayList<>();

    @Relationship(type = "PURCHASED", direction = Relationship.Direction.OUTGOING)
    private List<Purchased> purchases = new ArrayList<>();

    public Person() {}

    public Person(String name) {
        this.name = name;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<Person> getFollowing() { return following; }
    public void setFollowing(List<Person> following) { this.following = following; }

    public List<Purchased> getPurchases() { return purchases; }
    public void setPurchases(List<Purchased> purchases) { this.purchases = purchases; }
}
