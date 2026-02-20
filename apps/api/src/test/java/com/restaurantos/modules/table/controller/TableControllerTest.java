package com.restaurantos.modules.table.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurantos.modules.auth.service.JwtAuthenticationFilter;
import com.restaurantos.modules.auth.service.JwtService;
import com.restaurantos.modules.table.dto.request.TableRequest;
import com.restaurantos.modules.table.dto.response.TableResponse;
import com.restaurantos.modules.table.service.TableService;
import com.restaurantos.shared.config.SecurityConfig;

@WebMvcTest(TableController.class)
@ActiveProfiles("test")
@Import({ SecurityConfig.class, JwtAuthenticationFilter.class })
class TableControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TableService tableService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private UUID restaurantId;
    private UUID tableId;
    private TableResponse tableResponse;

    @BeforeEach
    void setUp() {
        restaurantId = UUID.randomUUID();
        tableId = UUID.randomUUID();
        tableResponse = TableResponse.builder()
                .id(tableId)
                .tableNumber(1)
                .capacity(4)
                .restaurantId(restaurantId)
                .status("EMPTY")
                .build();
    }

    @Test
    @WithMockUser
    void should_ReturnAllTables_When_Authenticated() throws Exception {
        when(tableService.getAllByRestaurant(restaurantId)).thenReturn(List.of(tableResponse));

        mockMvc.perform(get("/api/restaurants/{restaurantId}/tables", restaurantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].tableNumber").value(1));
    }

    @Test
    @WithMockUser(roles = "OWNER")
    void should_CreateTable_When_Owner() throws Exception {
        TableRequest request = TableRequest.builder()
                .tableNumber(1)
                .capacity(4)
                .build();

        when(tableService.create(eq(restaurantId), any(TableRequest.class))).thenReturn(tableResponse);

        mockMvc.perform(post("/api/restaurants/{restaurantId}/tables", restaurantId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.tableNumber").value(1));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void should_ReturnForbidden_When_CustomerCreatesTable() throws Exception {
        TableRequest request = TableRequest.builder()
                .tableNumber(1)
                .capacity(4)
                .build();

        mockMvc.perform(post("/api/restaurants/{restaurantId}/tables", restaurantId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "OWNER")
    void should_DeleteTable_When_Owner() throws Exception {
        mockMvc.perform(delete("/api/restaurants/{restaurantId}/tables/{id}", restaurantId, tableId)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(roles = "OWNER")
    void should_DownloadQrCodes_When_Owner() throws Exception {
        byte[] pdfContent = "test pdf".getBytes();
        when(tableService.generateQrCodesPdf(restaurantId)).thenReturn(pdfContent);

        mockMvc.perform(get("/api/restaurants/{restaurantId}/tables/qr-codes", restaurantId))
                .andExpect(status().isOk())
                .andExpect(header().string(org.springframework.http.HttpHeaders.CONTENT_TYPE,
                        MediaType.APPLICATION_PDF_VALUE))
                .andExpect(header().string(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
                        "form-data; name=\"attachment\"; filename=\"table-qr-codes.pdf\""))
                .andExpect(content().bytes(pdfContent));
    }
}
