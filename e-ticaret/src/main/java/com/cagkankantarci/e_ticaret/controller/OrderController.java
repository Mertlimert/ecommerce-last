package com.cagkankantarci.e_ticaret.controller;

import com.cagkankantarci.e_ticaret.dto.OrderStatusUpdateRequest;
import com.cagkankantarci.e_ticaret.entity.Order;
import com.cagkankantarci.e_ticaret.entity.User;
import com.cagkankantarci.e_ticaret.payload.response.MessageResponse;
import com.cagkankantarci.e_ticaret.repository.OrderRepository;
import com.cagkankantarci.e_ticaret.repository.UserRepository;
import com.cagkankantarci.e_ticaret.service.OrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private OrderService orderService;

    // Kullanıcının siparişlerini getir (sayfalama ile)
    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<Page<Order>> getUserOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        User currentUser = getCurrentUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateCreated").descending());
        Page<Order> orders = orderRepository.findByCustomerEmailOrderByDateCreatedDesc(currentUser.getEmail(), pageable);
        return ResponseEntity.ok(orders);
    }
    
    // Sipariş detayını ID'ye göre getir
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Sipariş bulunamadı: " + id));
        
        // Kullanıcı sadece kendi siparişlerini görebilmeli (Admin tüm siparişleri görebilir)
        if (!currentUser.getRoles().stream().anyMatch(r -> r.getName().name().equals("ROLE_ADMIN")) && 
            !order.getCustomer().getEmail().equals(currentUser.getEmail())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new MessageResponse("Bu siparişe erişim yetkiniz bulunmuyor"));
        }
        
        return ResponseEntity.ok(order);
    }
    
    // Tüm siparişleri listele (sadece admin)
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<Order>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dateCreated") String sort,
            @RequestParam(defaultValue = "desc") String direction) {
        
        Sort.Direction dir = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, sort));
        Page<Order> orders = orderRepository.findAll(pageable);
        return ResponseEntity.ok(orders);
    }
    
    // Sipariş durumunu güncelle (admin veya satıcı)
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody OrderStatusUpdateRequest statusRequest) {
        
        try {
            Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Sipariş bulunamadı: " + id));
            
            // Sipariş durumunu güncelle
            order.setStatus(statusRequest.getStatus());
            Order updatedOrder = orderRepository.save(order);
            
            return ResponseEntity.ok(updatedOrder);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Sipariş durumu güncellenirken bir hata oluştu: " + e.getMessage()));
        }
    }
    
    // Sipariş arama (Admin)
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<Order>> searchOrders(
            @RequestParam(required = false) String trackingNumber,
            @RequestParam(required = false) String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateCreated").descending());
        Page<Order> orders;
        
        if (trackingNumber != null && !trackingNumber.isEmpty()) {
            orders = orderService.findByTrackingNumber(trackingNumber, pageable);
        } else if (email != null && !email.isEmpty()) {
            orders = orderRepository.findByCustomerEmailOrderByDateCreatedDesc(email, pageable);
        } else {
            orders = orderRepository.findAll(pageable);
        }
        
        return ResponseEntity.ok(orders);
    }
    
    // Siparişi iptal et (kullanıcı kendi siparişini, admin herhangi bir siparişi)
    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasRole('USER') or hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        
        try {
            Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Sipariş bulunamadı: " + id));
            
            // Kullanıcı sadece kendi siparişlerini iptal edebilmeli (Admin tüm siparişleri iptal edebilir)
            if (!currentUser.getRoles().stream().anyMatch(r -> r.getName().name().equals("ROLE_ADMIN")) && 
                !order.getCustomer().getEmail().equals(currentUser.getEmail())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("Bu siparişi iptal etme yetkiniz bulunmuyor"));
            }
            
            // Sipariş durumunu "CANCELLED" olarak güncelle
            if ("DELIVERED".equalsIgnoreCase(order.getStatus())) {
                return ResponseEntity.badRequest()
                    .body(new MessageResponse("Teslim edilmiş bir sipariş iptal edilemez"));
            }
            
            order.setStatus("CANCELLED");
            Order updatedOrder = orderRepository.save(order);
            
            return ResponseEntity.ok(new MessageResponse("Sipariş başarıyla iptal edildi"));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Sipariş iptal edilirken bir hata oluştu: " + e.getMessage()));
        }
    }
    
    // Satıcıya ait siparişleri getir
    @GetMapping("/seller")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Page<Order>> getSellerOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        User currentUser = getCurrentUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateCreated").descending());
        Page<Order> orders = orderService.findOrdersBySellerId(currentUser.getId(), pageable);
        return ResponseEntity.ok(orders);
    }
    
    // Yardımcı metod: Şu anki oturumdaki kullanıcıyı al
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Kullanıcı bulunamadı: " + username));
    }
}