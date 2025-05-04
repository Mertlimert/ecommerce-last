package com.cagkankantarci.e_ticaret.service;

import com.cagkankantarci.e_ticaret.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    
    Page<Order> findByTrackingNumber(String trackingNumber, Pageable pageable);
    
    Page<Order> findOrdersBySellerId(Long sellerId, Pageable pageable);
}