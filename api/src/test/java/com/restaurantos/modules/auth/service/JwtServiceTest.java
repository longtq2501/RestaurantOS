package com.restaurantos.modules.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.restaurantos.modules.auth.entity.User;
import com.restaurantos.modules.auth.entity.UserRole;

class JwtServiceTest {

    private JwtService jwtService;
    private User user;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey",
                "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 3600000L); // 1 hour

        user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .role(UserRole.OWNER)
                .isActive(true)
                .build();
    }

    @Test
    void testGenerateAndExtractToken() {
        String token = jwtService.generateToken(user);
        assertNotNull(token);

        String extractedUsername = jwtService.extractUsername(token);
        assertEquals(user.getUsername(), extractedUsername);
    }

    @Test
    void testTokenValidation() {
        String token = jwtService.generateToken(user);
        assertTrue(jwtService.isTokenValid(token, user));
    }

    @Test
    void testTokenExpiration() {
        // Small expiration for testing
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", -1000L);
        String token = jwtService.generateToken(user);

        // Validation should fail or throw exception depending on library
        // JJWT parser throws ExpiredJwtException when parsing expired token
        assertThrows(Exception.class, () -> jwtService.isTokenValid(token, user));
    }
}
