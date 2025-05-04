package com.cagkankantarci.e_ticaret.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.cagkankantarci.e_ticaret.entity.User;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "cart")
@Data
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();
    
    @Column(name = "total_price")
    private BigDecimal totalPrice = BigDecimal.ZERO;
    
    @Column(name = "total_items")
    private int totalItems = 0;
    
    @Column(name = "date_created")
    @CreationTimestamp
    private Date dateCreated;
    
    @Column(name = "last_updated")
    @UpdateTimestamp
    private Date lastUpdated;
    
    // Sepet öğesi ekle
    public void addItem(CartItem item) {
        items.add(item);
        item.setCart(this);
        updateTotals();
    }
    
    // Sepet öğesi kaldır
    public void removeItem(CartItem item) {
        items.remove(item);
        item.setCart(null);
        updateTotals();
    }
    
    // Sepet toplamlarını güncelle
    public void updateTotals() {
        totalPrice = BigDecimal.ZERO;
        totalItems = 0;
        
        for (CartItem item : items) {
            totalPrice = totalPrice.add(item.getUnitPrice().multiply(new BigDecimal(item.getQuantity())));
            totalItems += item.getQuantity();
        }
    }
    
    // Sepeti temizle
    public void clear() {
        items.clear();
        totalPrice = BigDecimal.ZERO;
        totalItems = 0;
    }
}