package com.example.lab05.controller;

// ═══════════════════════════════════════════════════════════════
// TODO (Section 5 — Neo4j):
// Uncomment this entire controller after implementing
// SocialGraphService, PersonRepository, and Neo4jGraphRepository.
// ═══════════════════════════════════════════════════════════════

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.lab05.model.neo4j.Person;
import com.example.lab05.service.SocialGraphService;

@RestController
@RequestMapping("/api/graph/persons")
public class SocialGraphController {

    private final SocialGraphService socialGraphService;

    public SocialGraphController(SocialGraphService socialGraphService) {
        this.socialGraphService = socialGraphService;
    }

    @PostMapping
    public Person create(@RequestBody Map<String, String> body) {
        return socialGraphService.createPerson(body.get("name"));
    }

    @PostMapping("/{follower}/follow/{followee}")
    public Person follow(@PathVariable String follower, @PathVariable String followee) {
        return socialGraphService.follow(follower, followee);
    }

    @PostMapping("/{personName}/purchase")
    public Person purchase(@PathVariable String personName, @RequestBody Map<String, Object> body) {
        String productName = (String) body.get("productName");
        Integer quantity = (Integer) body.get("quantity");
        Double price = (Double) body.get("price");
        return socialGraphService.purchase(personName, productName, quantity, price);
    }

    @GetMapping("/{personName}/fof")
    public List<Person> friendsOfFriends(@PathVariable String personName) {
        return socialGraphService.getFriendsOfFriends(personName);
    }

    @GetMapping("/{personName}/recommendations")
    public List<Map<String, Object>> recommendations(
            @PathVariable String personName,
            @RequestParam(defaultValue = "5") int limit) {
        return socialGraphService.getRecommendations(personName, limit);
    }
}
