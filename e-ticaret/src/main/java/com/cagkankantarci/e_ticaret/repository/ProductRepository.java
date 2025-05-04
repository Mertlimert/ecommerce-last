package com.cagkankantarci.e_ticaret.repository;

import com.cagkankantarci.e_ticaret.entity.Product;
import com.cagkankantarci.e_ticaret.entity.ProductCategory;
import com.cagkankantarci.e_ticaret.entity.Seller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;


@RepositoryRestResource
public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByCategoryId(@Param("id") Long id, Pageable pageable);

    List<Product> findBySellerId(Long sellerId);
    
    Page<Product> findByNameContaining(@Param("name") String name, Pageable pageable);

    List<Product> findByCategoryId(Long categoryId);
    Page<Product> findByCategory(ProductCategory category, Pageable pageable);
    Page<Product> findBySeller(Seller seller, Pageable pageable);
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Product> findByUnitPriceBetween(BigDecimal min, BigDecimal max, Pageable pageable);
    Page<Product> findByActiveTrue(Pageable pageable);
    List<Product> findByNameContainingOrDescriptionContaining(String keyword, String keyword2);
}
