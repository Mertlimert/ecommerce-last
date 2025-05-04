package com.cagkankantarci.e_ticaret.payload.response;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.cagkankantarci.e_ticaret.entity.Cart;
import com.cagkankantarci.e_ticaret.entity.CartItem;

public class CartResponse {
    private Long id;
    private List<CartItemResponse> items = new ArrayList<>();
    private BigDecimal totalPrice;
    private int totalItems;
    
    public CartResponse() {
    }
    
    public CartResponse(Cart cart) {
        this.id = cart.getId();
        this.totalPrice = cart.getTotalPrice();
        this.totalItems = cart.getTotalItems();
        
        if (cart.getItems() != null) {
            this.items = cart.getItems().stream()
                .map(CartItemResponse::new)
                .collect(Collectors.toList());
        }
    }
    
    // Getter ve Setter metodları
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public List<CartItemResponse> getItems() {
        return items;
    }
    
    public void setItems(List<CartItemResponse> items) {
        this.items = items;
    }
    
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }
    
    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
    
    public int getTotalItems() {
        return totalItems;
    }
    
    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }
    
    // İç sınıf: Sepet öğesi yanıtı
    public static class CartItemResponse {
        private Long id;
        private Long productId;
        private String productName;
        private String productImage;
        private BigDecimal unitPrice;
        private int quantity;
        private BigDecimal totalPrice;
        
        public CartItemResponse() {
        }
        
        public CartItemResponse(CartItem item) {
            this.id = item.getId();
            this.productId = item.getProduct().getId();
            this.productName = item.getProduct().getName();
            this.productImage = item.getProduct().getImageUrl();
            this.unitPrice = item.getProduct().getUnitPrice();
            this.quantity = item.getQuantity();
            this.totalPrice = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
        }
        
        // Getter ve Setter metodları
        
        public Long getId() {
            return id;
        }
        
        public void setId(Long id) {
            this.id = id;
        }
        
        public Long getProductId() {
            return productId;
        }
        
        public void setProductId(Long productId) {
            this.productId = productId;
        }
        
        public String getProductName() {
            return productName;
        }
        
        public void setProductName(String productName) {
            this.productName = productName;
        }
        
        public String getProductImage() {
            return productImage;
        }
        
        public void setProductImage(String productImage) {
            this.productImage = productImage;
        }
        
        public BigDecimal getUnitPrice() {
            return unitPrice;
        }
        
        public void setUnitPrice(BigDecimal unitPrice) {
            this.unitPrice = unitPrice;
        }
        
        public int getQuantity() {
            return quantity;
        }
        
        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
        
        public BigDecimal getTotalPrice() {
            return totalPrice;
        }
        
        public void setTotalPrice(BigDecimal totalPrice) {
            this.totalPrice = totalPrice;
        }
    }
}