package com.cagkankantarci.e_ticaret.service.impl;

import com.cagkankantarci.e_ticaret.entity.Order;
import com.cagkankantarci.e_ticaret.repository.OrderRepository;
import com.cagkankantarci.e_ticaret.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;
    
    @Override
    public Page<Order> findByTrackingNumber(String trackingNumber, Pageable pageable) {
        return orderRepository.findByOrderTrackingNumberContainingIgnoreCase(trackingNumber, pageable);
    }
    
    @Override
    public Page<Order> findOrdersBySellerId(Long sellerId, Pageable pageable) {
        // Bu metot satıcıya ait siparişleri getirir
        // Satıcıya ait ürünlerin bulunduğu siparişleri getirmek için özel bir sorgu gerekebilir
        // Bu örnek için basit bir metot sağlandı, projenizin gerçek ihtiyaçlarına göre uyarlanmalıdır
        return orderRepository.findOrdersBySellerId(sellerId, pageable);
    }
}