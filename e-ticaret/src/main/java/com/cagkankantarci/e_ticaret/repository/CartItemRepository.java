package com.cagkankantarci.e_ticaret.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cagkankantarci.e_ticaret.entity.Cart;
import com.cagkankantarci.e_ticaret.entity.CartItem;
import com.cagkankantarci.e_ticaret.entity.Product;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
}