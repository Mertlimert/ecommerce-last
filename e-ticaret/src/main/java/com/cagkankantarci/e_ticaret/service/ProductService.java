package com.cagkankantarci.e_ticaret.service;

import com.cagkankantarci.e_ticaret.entity.Product;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<Product> findAll();
    
    Optional<Product> findById(Long id);
    
    List<Product> findBySellerId(Long sellerId);
    
    List<Product> findByCategoryId(Long categoryId);
    
    List<Product> searchProducts(String keyword);
    
    Product save(Product product);
    
    Product update(Long id, Product productDetails);
    
    void deleteById(Long id);
    
    Product getProductById(Long id);
}
