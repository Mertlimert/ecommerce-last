package com.cagkankantarci.e_ticaret.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cagkankantarci.e_ticaret.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    
}