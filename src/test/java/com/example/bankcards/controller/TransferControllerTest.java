package com.example.bankcards.controller;

import com.example.bankcards.config.SecurityConfig;
import com.example.bankcards.dto.TransferRequestDto;
import com.example.bankcards.dto.TransferResponseDto;
import com.example.bankcards.security.JwtService;
import com.example.bankcards.security.UserDetailsServiceImpl;
import com.example.bankcards.service.TransferService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransferController.class)
@Import(SecurityConfig.class)
class TransferControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private TransferService transferService;

    private final String token = "test.jwt.token";
    private final Long id = 100L;
    private final String mask = "**** **** **** 1234";
    private final BigDecimal amount = new BigDecimal(999);
    private final LocalDateTime time = LocalDateTime.MAX;

    private TransferResponseDto transferResp;
    private Page<TransferResponseDto> transfers;

    private void setupUserWithRole(String role) {
        Collection<? extends GrantedAuthority> adminAuthorities =
                List.of(new SimpleGrantedAuthority(role));
        UserDetails admin = new User("admin", "pass", adminAuthorities);
        when(userDetailsService.loadUserByUsername("admin")).thenReturn(admin);
    }

    @BeforeEach
    void setUp() {
        when(jwtService.extractUsername(token)).thenReturn("admin");
        when(jwtService.validateToken(token)).thenReturn(true);
        setupUserWithRole("ROLE_ADMIN");

        transferResp = TransferResponseDto.builder()
                .id(id)
                .payerMask(mask)
                .payeeMask(mask)
                .amount(amount)
                .created(time).build();
        transfers = new PageImpl<>(List.of(transferResp));
    }

    @Test
    void getTransfers_returnTransfersPaginated() throws Exception {
        when(transferService.getAll(any(Pageable.class))).thenReturn(transfers);
        mockMvc.perform(get("/api/transfers")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(id))
                .andExpect(jsonPath("$.content[0].payerMask").value(mask))
                .andExpect(jsonPath("$.content[0].payeeMask").value(mask))
                .andExpect(jsonPath("$.content[0].amount").value(amount))
                .andExpect(jsonPath("$.content[0].created").value(time.toString()));
    }

    @Test
    void getTransfers_AsUserForbidden() throws Exception {
        setupUserWithRole("ROLE_USER");
        when(transferService.getAll(any(Pageable.class))).thenReturn(transfers);
        mockMvc.perform(get("/api/transfers")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void getTransferById_returnTransfer() throws Exception {
        when(transferService.getTransferById(id)).thenReturn(transferResp);
        mockMvc.perform(get("/api/transfers/{id}", id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.payerMask").value(mask))
                .andExpect(jsonPath("$.payeeMask").value(mask))
                .andExpect(jsonPath("$.amount").value(amount))
                .andExpect(jsonPath("$.created").value(time.toString()));
    }

    @Test
    void getTransferById_AsUserForbidden() throws Exception {
        setupUserWithRole("ROLE_USER");
        when(transferService.getTransferById(id)).thenReturn(transferResp);
        mockMvc.perform(get("/api/transfers/{id}", id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void getCurrentTransfers_returnTransfersPaginated() throws Exception {
        when(transferService.getCurrentTransfers(
                any(Pageable.class), any(Principal.class))).thenReturn(transfers);
        mockMvc.perform(get("/api/transfers/current")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(id))
                .andExpect(jsonPath("$.content[0].payerMask").value(mask))
                .andExpect(jsonPath("$.content[0].payeeMask").value(mask))
                .andExpect(jsonPath("$.content[0].amount").value(amount))
                .andExpect(jsonPath("$.content[0].created").value(time.toString()));
    }

    @Test
    void getCurrentTransferById_returnTransfer() throws Exception {
        when(transferService.getCurrentTransferById(
                eq(id), any(Principal.class))).thenReturn(transferResp);
        mockMvc.perform(get("/api/transfers/current/{id}", id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.payerMask").value(mask))
                .andExpect(jsonPath("$.payeeMask").value(mask))
                .andExpect(jsonPath("$.amount").value(amount))
                .andExpect(jsonPath("$.created").value(time.toString()));
    }

    @Test
    void transfer_returnTransfer_AndStatusCreated() throws Exception {
        TransferRequestDto trRequest = TransferRequestDto.builder()
                .payerId(1L)
                .payeeId(1L)
                .amount(amount).build();

        when(transferService.transfer(
                any(Principal.class), any(TransferRequestDto.class))).thenReturn(transferResp);
        mockMvc.perform(post("/api/transfers")
                        .with(csrf())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/transfers/current/" + id))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.payerMask").value(mask))
                .andExpect(jsonPath("$.payeeMask").value(mask))
                .andExpect(jsonPath("$.amount").value(amount))
                .andExpect(jsonPath("$.created").value(time.toString()));
    }
}
