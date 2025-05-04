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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.cagkankantarci.e_ticaret.entity.User;
import com.cagkankantarci.e_ticaret.payload.request.PasswordChangeRequest;
import com.cagkankantarci.e_ticaret.payload.response.MessageResponse;
import com.cagkankantarci.e_ticaret.repository.UserRepository;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder encoder;
    
    // Kullanıcı profil bilgilerini getir
    @GetMapping("/profile")
    @PreAuthorize("hasRole('USER') or hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<?> getUserProfile() {
        User currentUser = getCurrentUser();
        // Şifreyi client'a göndermemek için null yapıyoruz
        currentUser.setPassword(null);
        return ResponseEntity.ok(currentUser);
    }
    
    // Kullanıcı profili güncelleme
    @PutMapping("/profile")
    @PreAuthorize("hasRole('USER') or hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<?> updateProfile(@RequestBody User userDetails) {
        User currentUser = getCurrentUser();
        
        // Güvenlik nedeniyle kullanıcı ID ve şifre değişikliği bu endpoint üzerinden yapılamaz
        userDetails.setId(currentUser.getId());
        userDetails.setPassword(currentUser.getPassword());
        userDetails.setUsername(currentUser.getUsername());
        userDetails.setEmail(currentUser.getEmail());
        userDetails.setRoles(currentUser.getRoles());
        
        User updatedUser = userRepository.save(userDetails);
        updatedUser.setPassword(null); // Şifreyi response'da gösterme
        
        return ResponseEntity.ok(updatedUser);
    }
    
    // Şifre değiştirme endpoint'i
    @PostMapping("/change-password") 
    @PreAuthorize("hasRole('USER') or hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<?> changePassword(@RequestBody PasswordChangeRequest request) {
        User currentUser = getCurrentUser();
        
        // Mevcut şifre kontrolü
        if (!encoder.matches(request.getCurrentPassword(), currentUser.getPassword())) {
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Hata: Mevcut şifre yanlış!"));
        }
        
        // Yeni şifreyi güncelle
        currentUser.setPassword(encoder.encode(request.getNewPassword()));
        userRepository.save(currentUser);
        
        return ResponseEntity.ok(new MessageResponse("Şifre başarıyla değiştirildi!"));
    }
    
    // Admin: Tüm kullanıcıları listele
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        
        // Güvenlik için şifreleri temizle
        users.forEach(user -> user.setPassword(null));
        
        return ResponseEntity.ok(users);
    }
    
    // Admin: Kullanıcı detaylarını ID'ye göre getir
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<User> userData = userRepository.findById(id);
        
        if (userData.isPresent()) {
            User user = userData.get();
            user.setPassword(null); // Şifreyi response'da gösterme
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new MessageResponse("Kullanıcı bulunamadı: " + id));
        }
    }
    
    // Admin: Kullanıcıyı devre dışı bırak/etkinleştir
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUserStatus(@PathVariable Long id, @RequestParam boolean active) {
        Optional<User> userData = userRepository.findById(id);
        
        if (userData.isPresent()) {
            User user = userData.get();
            user.setActive(active);
            
            User updatedUser = userRepository.save(user);
            updatedUser.setPassword(null); // Şifreyi response'da gösterme
            
            return ResponseEntity.ok(updatedUser);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new MessageResponse("Kullanıcı bulunamadı: " + id));
        }
    }
    
    // Admin: Kullanıcıyı sil
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        Optional<User> userData = userRepository.findById(id);
        
        if (userData.isPresent()) {
            userRepository.deleteById(id);
            
            Map<String, Boolean> response = new HashMap<>();
            response.put("deleted", Boolean.TRUE);
            
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new MessageResponse("Kullanıcı bulunamadı: " + id));
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