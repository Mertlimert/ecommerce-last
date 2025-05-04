package com.cagkankantarci.e_ticaret.service;

import com.cagkankantarci.e_ticaret.entity.User;
import com.cagkankantarci.e_ticaret.payload.request.LoginRequest;

public interface UserService {
    User login(LoginRequest loginRequest);

    User findByUsername(String name);
}