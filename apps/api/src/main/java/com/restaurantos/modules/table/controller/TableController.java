package com.restaurantos.modules.table.controller;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.restaurantos.modules.table.dto.request.TableRequest;
import com.restaurantos.modules.table.dto.response.TableResponse;
import com.restaurantos.modules.table.service.TableService;
import com.restaurantos.shared.dto.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller for managing restaurant tables.
 */
@RestController
@RequestMapping("/api/restaurants/{restaurantId}/tables")
@RequiredArgsConstructor
@Slf4j
public class TableController {

    private final TableService tableService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TableResponse>>> getAll(@PathVariable UUID restaurantId) {
        return ResponseEntity.ok(ApiResponse.success(tableService.getAllByRestaurant(restaurantId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TableResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(tableService.getById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public ResponseEntity<ApiResponse<TableResponse>> create(
            @PathVariable UUID restaurantId,
            @Valid @RequestBody TableRequest request) {
        TableResponse response = tableService.create(restaurantId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Table created successfully", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public ResponseEntity<ApiResponse<TableResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody TableRequest request) {
        TableResponse response = tableService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Table updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        tableService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Table deleted successfully", null));
    }

    @GetMapping("/qr-codes")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public ResponseEntity<byte[]> downloadQrCodes(@PathVariable UUID restaurantId) throws IOException {
        byte[] pdfContent = tableService.generateQrCodesPdf(restaurantId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "table-qr-codes.pdf");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
    }
}
