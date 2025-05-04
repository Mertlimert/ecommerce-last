package com.cagkankantarci.e_ticaret.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cagkankantarci.e_ticaret.entity.Seller;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Long> {
    
    Optional<Seller> findByUserId(Long userId);
    
    List<Seller> findByApproved(boolean approved);
}
