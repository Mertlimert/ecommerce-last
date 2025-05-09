package com.cagkankantarci.e_ticaret.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cagkankantarci.e_ticaret.entity.Cart;
import com.cagkankantarci.e_ticaret.entity.User;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
}