package com.example.lab05.service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.example.lab05.model.Product;
import com.example.lab05.repository.ProductRepository;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // TODO (Section 3 — Redis):
    // Add @Cacheable(value = "products", key = "#id") to this method.
    // Add a log statement: log.info("CACHE MISS: fetching product {} from DB", id);
    // You will need: private static final Logger log = LoggerFactory.getLogger(ProductService.class);
    @Cacheable(value = "products", key = "#id")
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found: " + id));
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    // TODO (Section 3 — Redis):
    // Add @CachePut(value = "products", key = "#product.id") to this method.
    @CachePut(value = "products", key = "#product.id")
    public Product updateProduct(Long id, Product product) {
        Product existing = getProductById(id);
        existing.setName(product.getName());
        existing.setCategory(product.getCategory());
        existing.setPrice(product.getPrice());
        existing.setStockQuantity(product.getStockQuantity());
        return productRepository.save(existing);
    }

    // TODO (Section 3 — Redis):
    // Add @CacheEvict(value = "products", key = "#id") to this method.
    @CacheEvict(value = "products", key = "#id")
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found: " + id);
        }
        productRepository.deleteById(id);
    }

    public List<Product> getByCategory(String category) {
        return productRepository.findByCategory(category);
    }
}
