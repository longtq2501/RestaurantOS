package com.restaurantos.modules.menu.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.restaurantos.modules.menu.dto.request.MenuItemRequest;
import com.restaurantos.modules.menu.dto.response.MenuItemResponse;
import com.restaurantos.modules.menu.service.MenuItemService;
import com.restaurantos.shared.dto.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST controller for managing menu items.
 */
@RestController
@RequestMapping("/api/restaurants/{restaurantId}/items")
@RequiredArgsConstructor
public class MenuItemController {

    private final MenuItemService menuItemService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<MenuItemResponse>>> getAll(
            @PathVariable UUID restaurantId,
            @RequestParam(required = false) UUID categoryId) {
        return ResponseEntity.ok(ApiResponse.success(menuItemService.getAll(restaurantId, categoryId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MenuItemResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(menuItemService.getById(id)));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<MenuItemResponse>>> search(
            @PathVariable UUID restaurantId,
            @RequestParam String q) {
        return ResponseEntity.ok(ApiResponse.success(menuItemService.search(restaurantId, q)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public ResponseEntity<ApiResponse<MenuItemResponse>> create(
            @PathVariable UUID restaurantId,
            @Valid @RequestBody MenuItemRequest request) {
        MenuItemResponse response = menuItemService.create(restaurantId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Item created successfully", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public ResponseEntity<ApiResponse<MenuItemResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody MenuItemRequest request) {
        MenuItemResponse response = menuItemService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Item updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        menuItemService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Item deleted successfully", null));
    }

    @PatchMapping("/{id}/availability")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public ResponseEntity<ApiResponse<MenuItemResponse>> toggleAvailability(@PathVariable UUID id) {
        MenuItemResponse response = menuItemService.toggleAvailability(id);
        return ResponseEntity.ok(ApiResponse.success("Availability updated successfully", response));
    }

    @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public ResponseEntity<ApiResponse<MenuItemResponse>> uploadImage(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) {
        MenuItemResponse response = menuItemService.uploadImage(id, file);
        return ResponseEntity.ok(ApiResponse.success("Image uploaded successfully", response));
    }

    @PostMapping("/bulk-update")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public ResponseEntity<ApiResponse<Void>> bulkUpdate(
            @RequestParam List<UUID> ids,
            @RequestParam String action) {
        menuItemService.bulkUpdate(ids, action);
        return ResponseEntity.ok(ApiResponse.success("Bulk update completed successfully", null));
    }
}
