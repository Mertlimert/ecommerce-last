package com.cagkankantarci.e_ticaret.service.impl;

import com.cagkankantarci.e_ticaret.entity.User;
import com.cagkankantarci.e_ticaret.payload.request.LoginRequest;
import com.cagkankantarci.e_ticaret.repository.UserRepository;
import com.cagkankantarci.e_ticaret.security.JwtUtils;
import com.cagkankantarci.e_ticaret.service.UserService;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public User login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return userRepository.findByUsername(loginRequest.getUsername())
            .orElseThrow(() -> new RuntimeException("Hata: Kullanıcı bulunamadı."));
    }

    @Override
    public User findByUsername(String name) {
        return userRepository.findByUsername(name)
            .orElseThrow(() -> new RuntimeException("Hata: Kullanıcı bulunamadı."));
    }
}

