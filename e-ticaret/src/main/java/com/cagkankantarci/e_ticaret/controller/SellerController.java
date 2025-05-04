package com.cagkankantarci.e_ticaret.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;
import com.cagkankantarci.e_ticaret.service.impl.SellerServiceImpl;
import com.cagkankantarci.e_ticaret.service.ProductService;
import com.cagkankantarci.e_ticaret.service.UserService;
import com.cagkankantarci.e_ticaret.entity.Seller;
import com.cagkankantarci.e_ticaret.entity.User;
import com.cagkankantarci.e_ticaret.exception.ResourceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import com.cagkankantarci.e_ticaret.entity.Product;
import com.cagkankantarci.e_ticaret.entity.Seller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;
import com.cagkankantarci.e_ticaret.service.SellerService;
import com.cagkankantarci.e_ticaret.service.ProductService;
import com.cagkankantarci.e_ticaret.service.UserService;
import com.cagkankantarci.e_ticaret.entity.Seller;
import com.cagkankantarci.e_ticaret.exception.ResourceNotFoundException;

@RestController
@RequestMapping("/api/seller")
public class SellerController {
    
    @Autowired
    private SellerService sellerService;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private UserService userService;
    
    // Satıcı profil işlemleri
    @GetMapping("/profile")
    public ResponseEntity<Seller> getSellerProfile(Principal principal) {
        User user = userService.findByUsername(principal.getName());
        Seller seller = sellerService.findByUserId(user.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Seller profile not found"));
        return ResponseEntity.ok(seller);
    }
    
    @PutMapping("/profile")
    public ResponseEntity<Seller> updateSellerProfile(@RequestBody Seller sellerDetails, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        Seller seller = sellerService.findByUserId(user.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Seller profile not found"));
        
        seller.setCompanyName(sellerDetails.getCompanyName());
        seller.setTaxId(sellerDetails.getTaxId());
        seller.setAddress(sellerDetails.getAddress());
        seller.setPhone(sellerDetails.getPhone());
        seller.setDescription(sellerDetails.getDescription());
        
        Seller updatedSeller = sellerService.save(seller);
        return ResponseEntity.ok(updatedSeller);
    }
    
    
    
   











// Ürün ile ilgili endpoint'leri SellerController.java dosyasına ekleyin

@PostMapping("/products")
public ResponseEntity<?> addProduct(@RequestBody Product product, Principal principal) {
    try {
        // Debug için gelen veriyi logla
        System.out.println("Gelen ürün: " + product);
        
        // Kullanıcı bilgisini al
        User user = userService.findByUsername(principal.getName());
        
        // Kullanıcının satıcı profili var mı kontrol et
        Seller seller = sellerService.findByUserId(user.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Satıcı profili bulunamadı"));
        
        // Seller ilişkisini kur
        product.setSeller(seller);
        
        // Ürünü kaydet
        Product savedProduct = productService.save(product);
        return ResponseEntity.ok(savedProduct);
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(Map.of("error", "Ürün eklenirken hata: " + e.getMessage()));
    }
}

@GetMapping("/products")
public ResponseEntity<List<Product>> getSellerProducts(Principal principal) {
    User user = userService.findByUsername(principal.getName());
    Seller seller = sellerService.findByUserId(user.getId())
        .orElseThrow(() -> new ResourceNotFoundException("Satıcı profili bulunamadı"));
    
    List<Product> products = productService.findBySellerId(seller.getId());
    return ResponseEntity.ok(products);
}

@PutMapping("/products/{id}")
public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody Product product, Principal principal) {
    try {
        User user = userService.findByUsername(principal.getName());
        Seller seller = sellerService.findByUserId(user.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Satıcı profili bulunamadı"));
        
        // Bu ürünün gerçekten bu satıcıya ait olduğunu kontrol et
        Product existingProduct = productService.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Ürün bulunamadı: " + id));
        
        if (!existingProduct.getSeller().getId().equals(seller.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "Bu ürünü düzenleme yetkiniz yok"));
        }
        
        // Ürün bilgilerini güncelle ama seller ilişkisini koru
        product.setId(id);
        product.setSeller(seller);
        
        Product updatedProduct = productService.save(product);
        return ResponseEntity.ok(updatedProduct);
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(Map.of("error", "Ürün güncellenirken hata: " + e.getMessage()));
    }
}

@DeleteMapping("/products/{id}")
public ResponseEntity<?> deleteProduct(@PathVariable Long id, Principal principal) {
    try {
        User user = userService.findByUsername(principal.getName());
        Seller seller = sellerService.findByUserId(user.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Satıcı profili bulunamadı"));
        
        // Bu ürünün gerçekten bu satıcıya ait olduğunu kontrol et
        Product existingProduct = productService.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Ürün bulunamadı: " + id));
        
        if (!existingProduct.getSeller().getId().equals(seller.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "Bu ürünü silme yetkiniz yok"));
        }
        
        productService.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Ürün başarıyla silindi"));
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(Map.of("error", "Ürün silinirken hata: " + e.getMessage()));
    }
}




    
    // Diğer ürün ve sipariş işlemleri...
}