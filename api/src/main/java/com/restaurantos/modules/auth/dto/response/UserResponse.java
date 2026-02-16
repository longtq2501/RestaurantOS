package com.restaurantos.modules.auth.dto.response;

import java.util.UUID;

import com.restaurantos.modules.auth.entity.UserRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response payload representing a user.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private UUID id;
    private String username;
    private String email;
    private String fullName;
    private UserRole role;
    private String roleDisplayName;
    private boolean isActive;
    private String avatarUrl;
}
