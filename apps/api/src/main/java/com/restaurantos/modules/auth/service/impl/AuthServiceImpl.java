package com.restaurantos.modules.auth.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restaurantos.modules.auth.dto.request.LoginRequest;
import com.restaurantos.modules.auth.dto.request.RefreshTokenRequest;
import com.restaurantos.modules.auth.dto.request.RegisterRequest;
import com.restaurantos.modules.auth.dto.response.AuthResponse;
import com.restaurantos.modules.auth.dto.response.UserResponse;
import com.restaurantos.modules.auth.entity.RefreshToken;
import com.restaurantos.modules.auth.entity.User;
import com.restaurantos.modules.auth.entity.UserRole;
import com.restaurantos.modules.auth.repository.RefreshTokenRepository;
import com.restaurantos.modules.auth.repository.UserRepository;
import com.restaurantos.modules.auth.service.AuthService;
import com.restaurantos.modules.auth.service.JwtService;
import com.restaurantos.modules.restaurant.entity.Restaurant;
import com.restaurantos.modules.restaurant.repository.RestaurantRepository;
import com.restaurantos.shared.exception.AlreadyExistsException;
import com.restaurantos.shared.exception.ResourceNotFoundException;
import com.restaurantos.shared.exception.UnauthorizedException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RestaurantRepository restaurantRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AlreadyExistsException("Username already exists: " + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AlreadyExistsException("Email already exists: " + request.getEmail());
        }

        // Create skeleton restaurant for the owner
        Restaurant restaurant = Restaurant.builder()
                .name(request.getRestaurantName())
                .build();
        restaurant = restaurantRepository.save(restaurant);

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role(UserRole.OWNER)
                .restaurant(restaurant)
                .isActive(true)
                .build();

        return mapToUserResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        User user = (User) authentication.getPrincipal();
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        String accessToken = jwtService.generateToken(user);
        RefreshToken refreshToken = createRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .user(mapToUserResponse(user))
                .build();
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (refreshToken.isExpired()) {
            refreshTokenRepository.delete(refreshToken);
            throw new UnauthorizedException("Refresh token expired");
        }

        User user = refreshToken.getUser();
        String accessToken = jwtService.generateToken(user);

        // Optional: Rotate refresh token
        refreshTokenRepository.delete(refreshToken);
        RefreshToken newRefreshToken = createRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken.getToken())
                .user(mapToUserResponse(user))
                .build();
    }

    @Override
    @Transactional
    public void logout(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        refreshTokenRepository.deleteByUser(user);
    }

    private RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiresAt(LocalDateTime.now().plusWeeks(1))
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .roleDisplayName(user.getRole().getDisplayName())
                .isActive(user.isActive())
                .avatarUrl(user.getAvatarUrl())
                .restaurantId(user.getRestaurant() != null ? user.getRestaurant().getId() : null)
                .build();
    }
}
