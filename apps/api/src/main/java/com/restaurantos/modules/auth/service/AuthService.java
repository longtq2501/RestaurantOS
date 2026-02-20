package com.restaurantos.modules.auth.service;

import java.util.UUID;

import com.restaurantos.modules.auth.dto.request.LoginRequest;
import com.restaurantos.modules.auth.dto.request.RefreshTokenRequest;
import com.restaurantos.modules.auth.dto.request.RegisterRequest;
import com.restaurantos.modules.auth.dto.response.AuthResponse;
import com.restaurantos.modules.auth.dto.response.UserResponse;

public interface AuthService {

    UserResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(RefreshTokenRequest request);

    void logout(UUID userId);
}
