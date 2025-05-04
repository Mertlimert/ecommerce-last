package com.cagkankantarci.e_ticaret.config;

import com.cagkankantarci.e_ticaret.entity.ERole;
import com.cagkankantarci.e_ticaret.entity.ProductCategory;
import com.cagkankantarci.e_ticaret.entity.Role;
import com.cagkankantarci.e_ticaret.entity.User;

import com.cagkankantarci.e_ticaret.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductCategoryRepository categoryRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Override
    public void run(String... args) throws Exception {
        // Varsayılan rolleri oluştur
        createRoles();
        createAdminUser();
        createSellerUser();
        createSampleCategories();
    }

    private void createRoles() {
        if (roleRepository.count() == 0) {
            Role userRole = new Role();
            userRole.setName(ERole.ROLE_USER);
            roleRepository.save(userRole);

            Role adminRole = new Role();
            adminRole.setName(ERole.ROLE_ADMIN);
            roleRepository.save(adminRole);

            Role sellerRole = new Role();
            sellerRole.setName(ERole.ROLE_SELLER);
            roleRepository.save(sellerRole);

            System.out.println("Default roller başarıyla oluşturuldu!");
        }
    }

    private void createAdminUser() {
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@example.com");
            admin.setPassword(encoder.encode("admin123"));
            admin.setFirstName("Admin");
            admin.setLastName("User");

            Set<Role> roles = new HashSet<>();
            roleRepository.findByName(ERole.ROLE_ADMIN).ifPresent(roles::add);
            roleRepository.findByName(ERole.ROLE_USER).ifPresent(roles::add);
            admin.setRoles(roles);

            userRepository.save(admin);

            System.out.println("Admin kullanıcı başarıyla oluşturuldu!");
        }
    }

    private void createSellerUser() {
        if (!userRepository.existsByUsername("seller")) {
            User seller = new User();
            seller.setUsername("seller");
            seller.setEmail("seller@example.com");
            seller.setPassword(encoder.encode("seller123"));
            seller.setFirstName("Satıcı");
            seller.setLastName("Kullanıcı");

            Set<Role> roles = new HashSet<>();
            roleRepository.findByName(ERole.ROLE_SELLER).ifPresent(roles::add);
            roleRepository.findByName(ERole.ROLE_USER).ifPresent(roles::add);
            seller.setRoles(roles);

            userRepository.save(seller);

            System.out.println("Satıcı kullanıcı başarıyla oluşturuldu!");
        }
    }

    private void createSampleCategories() {
        if (categoryRepository.count() == 0) {
            // create electronic category
            ProductCategory electronic = new ProductCategory();
            electronic.setCategoryName("Elektronik");
            categoryRepository.save(electronic);

            // create clothing category
            ProductCategory clothing = new ProductCategory();
            clothing.setCategoryName("Giyim");
            categoryRepository.save(clothing);

            // create books category
            ProductCategory books = new ProductCategory();
            books.setCategoryName("Kitaplar");
            categoryRepository.save(books);

            System.out.println("Örnek kategoriler başarıyla oluşturuldu!");
        }
    }
}
