package com.cagkankantarci.e_ticaret.repository;

import com.cagkankantarci.e_ticaret.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByCustomerEmailOrderByDateCreatedDesc(@Param("email") String email, Pageable pageable);
    Page<Order> findByOrderTrackingNumberContainingIgnoreCase(String trackingNumber, Pageable pageable);
    @Query("SELECT o FROM Order o JOIN o.orderItems oi JOIN Product p ON oi.productId = p.id WHERE p.seller.id = :sellerId ORDER BY o.dateCreated DESC")
    Page<Order> findOrdersBySellerId(@Param("sellerId") Long sellerId, Pageable pageable);

}
