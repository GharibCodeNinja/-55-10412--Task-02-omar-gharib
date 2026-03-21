package com.example.lab05.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.lab05.model.neo4j.Neo4jProduct;
import com.example.lab05.model.neo4j.Person;
import com.example.lab05.model.neo4j.Purchased;
import com.example.lab05.repository.neo4j.Neo4jGraphRepository;
import com.example.lab05.repository.neo4j.PersonRepository;

@Service
public class SocialGraphService {

    // TODO: Inject PersonRepository via constructor injection
    // TODO: Inject Neo4jGraphRepository via constructor injection
    private final PersonRepository personRepository;
    private final Neo4jGraphRepository neo4jGraphRepository;
    public SocialGraphService(PersonRepository personRepository, Neo4jGraphRepository neo4jGraphRepository) {
        this.personRepository = personRepository;
        this.neo4jGraphRepository = neo4jGraphRepository;
    }

    // TODO: Implement createPerson(String name)
    //   - Create a new Person with the given name, save via PersonRepository, return it
    public Person createPerson(String name) {
        Person person = new Person(name);
        return personRepository.save(person);
    }

    // TODO: Implement follow(String followerName, String followeeName)
    //   - Find both persons by name via PersonRepository (throw if not found)
    //   - Add followee to follower's following list
    //   - Save the follower via PersonRepository

    public Person follow(String followerName, String followeeName) {
        Person follower = personRepository.findByName(followerName)
            .orElseThrow(() -> new RuntimeException("Follower not found: " + followerName));
        Person followee = personRepository.findByName(followeeName)
            .orElseThrow(() -> new RuntimeException("Followee not found: " + followeeName));
        follower.getFollowing().add(followee);
        personRepository.save(follower);
        return follower;
    }

    // TODO: Implement purchase(String personName, String productName, Integer quantity)
    //   - Find the person by name via PersonRepository (throw if not found)
    //   - Create a new Neo4jProduct and a Purchased relationship
    //   - Add to person's purchases list
    //   - Save the person via PersonRepository
    public Person purchase(String personName, String productName, Integer quantity, Double price) {
        Person person = personRepository.findByName(personName)
            .orElseThrow(() -> new RuntimeException("Person not found: " + personName));
        Neo4jProduct product = new Neo4jProduct(productName, price);
        Purchased purchased = new Purchased(product, quantity);
        person.getPurchases().add(purchased);
        personRepository.save(person);
        return person;
    }

    // TODO: Implement getFriendsOfFriends(String personName)
    //   - Delegate to PersonRepository (Pattern 2: @Query Cypher)
    public List<Person> getFriendsOfFriends(String personName) {
        return personRepository.findFriendsOfFriends(personName);
    }

    // TODO: Implement getRecommendations(String personName, int limit)
    //   - Delegate to Neo4jGraphRepository (Pattern 3: Neo4jClient)
    public List<Map<String, Object>> getRecommendations(String personName, int limit) {
        return neo4jGraphRepository.getRecommendations(personName, limit);
    }
}
