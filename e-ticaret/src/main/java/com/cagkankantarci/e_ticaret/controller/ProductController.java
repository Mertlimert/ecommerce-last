package com.cagkankantarci.e_ticaret.controller;

import com.cagkankantarci.e_ticaret.entity.Product;
import com.cagkankantarci.e_ticaret.entity.ProductCategory;
import com.cagkankantarci.e_ticaret.entity.Seller;
import com.cagkankantarci.e_ticaret.repository.ProductCategoryRepository;
import com.cagkankantarci.e_ticaret.repository.ProductRepository;
import com.cagkankantarci.e_ticaret.repository.SellerRepository;
import com.cagkankantarci.e_ticaret.service.ProductService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private SellerRepository sellerRepository;
    
    @Autowired
    private ProductCategoryRepository categoryRepository;
    
    // Tüm ürünleri getir (sayfalama ile)
    @GetMapping
    public ResponseEntity<Page<Product>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        Page<Product> products = productRepository.findAll(pageable);
        return ResponseEntity.ok(products);
    }
    
    // ID'ye göre ürün getir
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Ürün bulunamadı: " + id));
        
        return ResponseEntity.ok(product);
    }
    
    // Kategori ID'sine göre ürünleri getir
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<Product>> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
            
        Pageable pageable = PageRequest.of(page, size);
        ProductCategory category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Kategori bulunamadı: " + categoryId));
                
        Page<Product> products = productRepository.findByCategory(category, pageable);
        return ResponseEntity.ok(products);
    }
    
    // Satıcı ID'sine göre ürünleri getir
    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<Page<Product>> getProductsBySeller(
            @PathVariable Long sellerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
            
        Pageable pageable = PageRequest.of(page, size);
        Seller seller = sellerRepository.findById(sellerId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Satıcı bulunamadı: " + sellerId));
                
        Page<Product> products = productRepository.findBySeller(seller, pageable);
        return ResponseEntity.ok(products);
    }
    
    // İsme göre ürün ara
    @GetMapping("/search")
    public ResponseEntity<Page<Product>> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
            
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productRepository.findByNameContainingIgnoreCase(keyword, pageable);
        return ResponseEntity.ok(products);
    }
    
    // Fiyat aralığına göre ürünleri getir
    @GetMapping("/price-range")
    public ResponseEntity<Page<Product>> getProductsByPriceRange(
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
            
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productRepository.findByUnitPriceBetween(min, max, pageable);
        return ResponseEntity.ok(products);
    }
    
    // Aktif ürünleri getir
    @GetMapping("/active")
    public ResponseEntity<Page<Product>> getActiveProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
            
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productRepository.findByActiveTrue(pageable);
        return ResponseEntity.ok(products);
    }
    
    // Yeni ürün ekle
    @PostMapping
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        if (product.getId() != null) {
            product.setId(null); // Yeni ürün olduğu için ID null olmalı
        }
        
        // Kategori ve satıcı kontrolü
        if (product.getCategory() == null || product.getCategory().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ürün kategorisi belirtilmelidir");
        }
        
        if (product.getSeller() == null || product.getSeller().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Satıcı belirtilmelidir");
        }
        
        // Kategori ve satıcının var olup olmadığını kontrol et
        categoryRepository.findById(product.getCategory().getId())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Kategori bulunamadı: " + product.getCategory().getId()));
                
        sellerRepository.findById(product.getSeller().getId())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Satıcı bulunamadı: " + product.getSeller().getId()));
        
        Product savedProduct = productRepository.save(product);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }
    
    // Ürün güncelle
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        Product existingProduct = productRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Ürün bulunamadı: " + id));
                
        // ID'nin değiştirilmemesini sağla
        product.setId(id);
        
        // Kategori ve satıcının var olup olmadığını kontrol et
        if (product.getCategory() != null && product.getCategory().getId() != null) {
            categoryRepository.findById(product.getCategory().getId())
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Kategori bulunamadı: " + product.getCategory().getId()));
        }
        
        if (product.getSeller() != null && product.getSeller().getId() != null) {
            sellerRepository.findById(product.getSeller().getId())
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Satıcı bulunamadı: " + product.getSeller().getId()));
        }
        
        Product updatedProduct = productRepository.save(product);
        return ResponseEntity.ok(updatedProduct);
    }
    
    // Ürün sil
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Ürün bulunamadı: " + id));
                
        productRepository.delete(product);
        return ResponseEntity.ok().build();
    }
    
    // Ürün durumunu değiştir (aktif/pasif)
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<Product> updateProductStatus(
            @PathVariable Long id, 
            @RequestParam boolean active) {
            
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Ürün bulunamadı: " + id));
                
        product.setActive(active);
        Product updatedProduct = productRepository.save(product);
        return ResponseEntity.ok(updatedProduct);
    }
}