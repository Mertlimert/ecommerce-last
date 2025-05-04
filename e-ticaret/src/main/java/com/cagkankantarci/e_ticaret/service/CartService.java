package com.cagkankantarci.e_ticaret.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cagkankantarci.e_ticaret.entity.Cart;
import com.cagkankantarci.e_ticaret.entity.CartItem;
import com.cagkankantarci.e_ticaret.entity.Product;
import com.cagkankantarci.e_ticaret.entity.User;
import com.cagkankantarci.e_ticaret.repository.CartItemRepository;
import com.cagkankantarci.e_ticaret.repository.CartRepository;

@Service
public class CartService {
    
    @Autowired
    private CartRepository cartRepository;
    
    @Autowired
    private CartItemRepository cartItemRepository;
    
    // Kullanıcının sepetini getir veya yeni oluştur
    @Transactional
    public Cart getOrCreateCart(User user) {
        Optional<Cart> existingCart = cartRepository.findByUser(user);
        
        if (existingCart.isPresent()) {
            return existingCart.get();
        }
        
        // Yeni sepet oluştur
        Cart newCart = new Cart();
        newCart.setUser(user);
        return cartRepository.save(newCart);
    }
    
    // Sepete ürün ekle
    @Transactional
    public CartItem addItemToCart(User user, Product product, int quantity) {
        Cart cart = getOrCreateCart(user);
        
        // Ürün zaten sepette var mı kontrol et
        Optional<CartItem> existingItem = cartItemRepository.findByCartAndProduct(cart, product);
        
        CartItem cartItem;
        
        if (existingItem.isPresent()) {
            // Mevcut öğenin miktarını güncelle
            cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            // Yeni sepet öğesi oluştur
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setUnitPrice(product.getUnitPrice());
            
            cart.getItems().add(cartItem);
        }
        
        // Sepet öğesini kaydet
        cartItemRepository.save(cartItem);
        
        // Sepet toplamlarını güncelle
        updateCartTotals(cart);
        
        return cartItem;
    }
    
    // Sepet toplamlarını güncelle
    @Transactional
    public void updateCartTotals(Cart cart) {
        cart.updateTotals();
        cartRepository.save(cart);
    }
    
    // Sepeti temizle
    @Transactional
    public void clearCart(User user) {
        Cart cart = getOrCreateCart(user);
        cart.getItems().clear();
        cart.setTotalPrice(java.math.BigDecimal.ZERO);
        cart.setTotalItems(0);
        cartRepository.save(cart);
    }
}