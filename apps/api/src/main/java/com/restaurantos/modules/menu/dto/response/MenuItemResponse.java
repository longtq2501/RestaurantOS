package com.restaurantos.modules.menu.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for menu item details.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemResponse {
    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    private String thumbnailUrl;
    private Integer prepTime;
    private Integer spicyLevel;
    private Boolean isVegetarian;
    private String allergens;
    private Boolean isAvailable;
    private Integer displayOrder;
    private Boolean isFeatured;
    private Long orderCount;
    private BigDecimal ratingAvg;
    private Long ratingCount;
    private UUID categoryId;
    private String categoryName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
