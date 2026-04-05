package com.example.lab05.controller;

// ═══════════════════════════════════════════════════════════════
// TODO (Section 6 — Elasticsearch):
// Uncomment this entire controller after implementing
// ProductSearchService, ProductSearchRepository, and
// ElasticSearchQueryRepository.
// ═══════════════════════════════════════════════════════════════

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.lab05.model.elastic.ProductDocument;
import com.example.lab05.service.ProductSearchService;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final ProductSearchService searchService;

    public SearchController(ProductSearchService searchService) {
        this.searchService = searchService;
    }

    @PostMapping("/products")
    public ProductDocument index(@RequestBody ProductDocument product) {
        return searchService.saveProduct(product);
    }

    // Pattern 1: Derived query
    @GetMapping("/products/category/{category}")
    public List<ProductDocument> byCategory(@PathVariable String category) {
        return searchService.getByCategory(category);
    }

    // Pattern 2: @Query JSON fuzzy match
    @GetMapping("/products/name")
    public List<ProductDocument> byName(@RequestParam String q) {
        return searchService.searchByName(q);
    }

    // Pattern 3: Full NativeQuery search
    @GetMapping
    public List<ProductDocument> search(
            @RequestParam String q,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return searchService.search(q, category, minPrice, maxPrice, page, size);
    }
}
