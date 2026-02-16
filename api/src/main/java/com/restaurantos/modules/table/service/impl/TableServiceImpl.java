package com.restaurantos.modules.table.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restaurantos.modules.restaurant.entity.Restaurant;
import com.restaurantos.modules.restaurant.repository.RestaurantRepository;
import com.restaurantos.modules.table.dto.request.TableRequest;
import com.restaurantos.modules.table.dto.response.TableResponse;
import com.restaurantos.modules.table.entity.RestaurantTable;
import com.restaurantos.modules.table.repository.TableRepository;
import com.restaurantos.modules.table.service.PDFService;
import com.restaurantos.modules.table.service.QRCodeService;
import com.restaurantos.modules.table.service.TableService;
import com.restaurantos.shared.exception.AlreadyExistsException;
import com.restaurantos.shared.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of {@link TableService}.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TableServiceImpl implements TableService {

    private final TableRepository tableRepository;
    private final RestaurantRepository restaurantRepository;
    private final QRCodeService qrCodeService;
    private final PDFService pdfService;

    @Override
    @Transactional(readOnly = true)
    public List<TableResponse> getAllByRestaurant(UUID restaurantId) {
        return tableRepository.findByRestaurantIdOrderByTableNumberAsc(restaurantId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TableResponse getById(UUID id) {
        RestaurantTable table = tableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Table not found with id: " + id));
        return mapToResponse(table);
    }

    @Override
    @Transactional
    public TableResponse create(UUID restaurantId, TableRequest request) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + restaurantId));

        if (tableRepository.existsByRestaurantIdAndTableNumber(restaurantId, request.getTableNumber())) {
            throw new AlreadyExistsException(
                    "Table number " + request.getTableNumber() + " already exists in this restaurant");
        }

        RestaurantTable table = RestaurantTable.builder()
                .tableNumber(request.getTableNumber())
                .capacity(request.getCapacity())
                .section(request.getSection())
                .restaurant(restaurant)
                .qrCodeToken(UUID.randomUUID().toString()) // Initial token, can be refined
                .build();

        return mapToResponse(tableRepository.save(table));
    }

    @Override
    @Transactional
    public TableResponse update(UUID id, TableRequest request) {
        RestaurantTable table = tableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Table not found with id: " + id));

        if (!table.getTableNumber().equals(request.getTableNumber()) &&
                tableRepository.existsByRestaurantIdAndTableNumber(table.getRestaurant().getId(),
                        request.getTableNumber())) {
            throw new AlreadyExistsException(
                    "Table number " + request.getTableNumber() + " already exists in this restaurant");
        }

        table.setTableNumber(request.getTableNumber());
        table.setCapacity(request.getCapacity());
        table.setSection(request.getSection());

        return mapToResponse(tableRepository.save(table));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!tableRepository.existsById(id)) {
            throw new ResourceNotFoundException("Table not found with id: " + id);
        }
        tableRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] generateQrCodesPdf(UUID restaurantId) throws IOException {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + restaurantId));

        List<RestaurantTable> tables = tableRepository.findByRestaurantIdOrderByTableNumberAsc(restaurantId);

        List<PDFService.TableQrInfo> qrInfos = tables.stream().map(table -> {
            try {
                // In a real app, this URL would point to the customer ordering page
                String qrUrl = "https://restaurant-os.com/order/" + table.getQrCodeToken();
                byte[] qrImage = qrCodeService.generateQRCode(qrUrl, 300, 300);
                return new PDFService.TableQrInfo(table.getTableNumber().toString(), qrImage);
            } catch (IOException e) {
                log.error("Failed to generate QR code for table {}", table.getTableNumber(), e);
                return null;
            }
        }).filter(java.util.Objects::nonNull).collect(Collectors.toList());

        return pdfService.generateQrCodeGridPdf(restaurant.getName(), qrInfos);
    }

    private TableResponse mapToResponse(RestaurantTable table) {
        return TableResponse.builder()
                .id(table.getId())
                .tableNumber(table.getTableNumber())
                .capacity(table.getCapacity())
                .qrCodeToken(table.getQrCodeToken())
                .status(table.getStatus().name())
                .section(table.getSection())
                .currentOrderId(table.getCurrentOrderId())
                .restaurantId(table.getRestaurant().getId())
                .createdAt(table.getCreatedAt())
                .updatedAt(table.getUpdatedAt())
                .build();
    }
}
