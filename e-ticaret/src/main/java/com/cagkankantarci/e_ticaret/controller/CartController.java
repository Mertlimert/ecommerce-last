package com.cagkankantarci.e_ticaret.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.cagkankantarci.e_ticaret.entity.Cart;
import com.cagkankantarci.e_ticaret.entity.CartItem;
import com.cagkankantarci.e_ticaret.entity.Product;
import com.cagkankantarci.e_ticaret.entity.User;
import com.cagkankantarci.e_ticaret.payload.request.AddToCartRequest;
import com.cagkankantarci.e_ticaret.payload.request.UpdateCartItemRequest;
import com.cagkankantarci.e_ticaret.payload.response.CartResponse;
import com.cagkankantarci.e_ticaret.payload.response.MessageResponse;
import com.cagkankantarci.e_ticaret.repository.CartItemRepository;
import com.cagkankantarci.e_ticaret.repository.CartRepository;
import com.cagkankantarci.e_ticaret.repository.ProductRepository;
import com.cagkankantarci.e_ticaret.repository.UserRepository;
import com.cagkankantarci.e_ticaret.service.CartService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartRepository cartRepository;
    
    @Autowired
    private CartItemRepository cartItemRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CartService cartService;
    
    // Kullanıcının sepetini getir
    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<?> getCart() {
        User currentUser = getCurrentUser();
        Cart cart = cartService.getOrCreateCart(currentUser);
        
        CartResponse response = new CartResponse(cart);
        return ResponseEntity.ok(response);
    }
    
    // Sepete ürün ekle
    @PostMapping("/add")
    @PreAuthorize("hasRole('USER') or hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<?> addToCart(@RequestBody AddToCartRequest request) {
        User currentUser = getCurrentUser();
        
        try {
            Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Ürün bulunamadı: " + request.getProductId()));
            
            // Ürünün stok kontrolü
            if (product.getUnitsInStock() < request.getQuantity()) {
                return ResponseEntity.badRequest()
                    .body(new MessageResponse("Yetersiz stok! Mevcut stok: " + product.getUnitsInStock()));
            }
            
            CartItem cartItem = cartService.addItemToCart(currentUser, product, request.getQuantity());
            return ResponseEntity.ok(new MessageResponse("Ürün sepete başarıyla eklendi"));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Ürün sepete eklenirken bir hata oluştu: " + e.getMessage()));
        }
    }
    
    // Sepet öğesini güncelle (miktar)
    @PutMapping("/items/{itemId}")
    @PreAuthorize("hasRole('USER') or hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<?> updateCartItem(
            @PathVariable Long itemId,
            @RequestBody UpdateCartItemRequest request) {
        
        User currentUser = getCurrentUser();
        
        try {
            CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Sepet öğesi bulunamadı: " + itemId));
            
            // Öğenin kullanıcıya ait olup olmadığını kontrol et
            if (!cartItem.getCart().getUser().getId().equals(currentUser.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("Bu sepet öğesini güncelleme yetkiniz yok"));
            }
            
            // Stok kontrolü
            Product product = cartItem.getProduct();
            if (product.getUnitsInStock() < request.getQuantity()) {
                return ResponseEntity.badRequest()
                    .body(new MessageResponse("Yetersiz stok! Mevcut stok: " + product.getUnitsInStock()));
            }
            
            cartItem.setQuantity(request.getQuantity());
            cartItemRepository.save(cartItem);
            
            // Sepet toplamını güncelle
            Cart cart = cartItem.getCart();
            cartService.updateCartTotals(cart);
            
            return ResponseEntity.ok(new MessageResponse("Sepet öğesi güncellendi"));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Sepet öğesi güncellenirken bir hata oluştu: " + e.getMessage()));
        }
    }
    
    // Sepetten öğe kaldır
    @DeleteMapping("/items/{itemId}")
    @PreAuthorize("hasRole('USER') or hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<?> removeCartItem(@PathVariable Long itemId) {
        User currentUser = getCurrentUser();
        
        try {
            CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Sepet öğesi bulunamadı: " + itemId));
            
            // Öğenin kullanıcıya ait olup olmadığını kontrol et
            if (!cartItem.getCart().getUser().getId().equals(currentUser.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("Bu sepet öğesini silme yetkiniz yok"));
            }
            
            Cart cart = cartItem.getCart();
            cartItemRepository.delete(cartItem);
            
            // Sepet toplamını güncelle
            cartService.updateCartTotals(cart);
            
            return ResponseEntity.ok(new MessageResponse("Ürün sepetten kaldırıldı"));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Sepet öğesi silinirken bir hata oluştu: " + e.getMessage()));
        }
    }
    
    // Sepeti temizle
    @DeleteMapping("/clear")
    @PreAuthorize("hasRole('USER') or hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<?> clearCart() {
        User currentUser = getCurrentUser();
        
        try {
            cartService.clearCart(currentUser);
            return ResponseEntity.ok(new MessageResponse("Sepet başarıyla temizlendi"));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Sepet temizlenirken bir hata oluştu: " + e.getMessage()));
        }
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