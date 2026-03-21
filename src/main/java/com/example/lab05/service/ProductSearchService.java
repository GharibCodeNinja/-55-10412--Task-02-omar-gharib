package com.example.lab05.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.lab05.model.elastic.ProductDocument;
import com.example.lab05.repository.elastic.ElasticSearchQueryRepository;
import com.example.lab05.repository.elastic.ProductSearchRepository;

@Service
public class ProductSearchService {

    // TODO: Inject ProductSearchRepository via constructor injection
    // TODO: Inject ElasticSearchQueryRepository via constructor injection
    
    private final ProductSearchRepository productSearchRepository;
    private final ElasticSearchQueryRepository elasticSearchQueryRepository;
    public ProductSearchService(ProductSearchRepository productSearchRepository,
                                ElasticSearchQueryRepository elasticSearchQueryRepository) {
        this.productSearchRepository = productSearchRepository;
        this.elasticSearchQueryRepository = elasticSearchQueryRepository;
    }
    // TODO: Implement saveProduct(ProductDocument product)
    //   - Save using ProductSearchRepository and return
    public ProductDocument saveProduct(ProductDocument product) {
        return productSearchRepository.save(product);
    }

    // TODO: Implement getByCategory(String category)
    //   - Delegate to ProductSearchRepository (Pattern 1: derived query)
    public List<ProductDocument> getByCategory(String category) {
        return productSearchRepository.findByCategory(category);
    }

    // TODO: Implement searchByName(String name)
    //   - Delegate to ProductSearchRepository (Pattern 2: @Query JSON)
    public List<ProductDocument> searchByName(String name) {
        return productSearchRepository.searchByName(name);
    }
    // TODO: Implement search(String query, String category,
    //                        Double minPrice, Double maxPrice,
    //                        int page, int size)
    //   - Delegate to ElasticSearchQueryRepository (Pattern 3: ElasticsearchOperations)
    public List<ProductDocument> search(String query, String category,
                                        Double minPrice, Double maxPrice,
                                        int page, int size) {
        return elasticSearchQueryRepository.search(query, category, minPrice, maxPrice, page, size);
    }
}
