package com.example.bankcards.controller;

import com.example.bankcards.config.SecurityConfig;
import com.example.bankcards.dto.BankCardCreateDto;
import com.example.bankcards.dto.BankCardResponseDto;
import com.example.bankcards.dto.BankCardUpdateDto;
import com.example.bankcards.enums.CardStatus;
import com.example.bankcards.security.JwtService;
import com.example.bankcards.security.UserDetailsServiceImpl;
import com.example.bankcards.service.BankCardService;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.springframework.test.web.servlet.RequestBuilder;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collection;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BankCardController.class)
@Import(SecurityConfig.class)
public class BankCardControllerTest {

    private static final String MASK = "**** **** **** 1234";
    private static final String OWNER = "owner";
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BankCardService bankCardService;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private JwtService jwtService;

    private BankCardResponseDto bankCardResponseDto;
    private final Long id = 1L;
    private final String token = "test.jwt.token";

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
        bankCardResponseDto = BankCardResponseDto.builder()
                .id(id)
                .owner(OWNER)
                .maskedNumber(MASK).build();

    }

    private RequestBuilder requestBuilderGet(Long id) {
        when(bankCardService.getBankCardById(id)).thenReturn(bankCardResponseDto);
        return get("/api/cards/{id}", id)
                .header("Authorization", "Bearer " + token);
    }

    @Test
    void getBankCardById_returnsCard() throws Exception {
        mockMvc.perform(requestBuilderGet(id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.owner").value(OWNER))
                .andExpect(jsonPath("$.maskedNumber").value(MASK));
    }

    @Test
    void getBankCard_AsUser_Forbidden() throws Exception {
        setupUserWithRole("ROLE_USER");
        mockMvc.perform(requestBuilderGet(id))
                .andExpect(status().isForbidden());
    }

    private RequestBuilder getCardsReqBuilder() {
        Page<BankCardResponseDto> pageCards = new PageImpl<>(List.of(bankCardResponseDto));
        when(bankCardService.getCards(any(Pageable.class))).thenReturn(pageCards);
        return get("/api/cards")
                .header("Authorization", "Bearer " + token)
                .param("page", "0")
                .param("size", "10");
    }

    @Test
    void getCards_returnsPage() throws Exception {
        mockMvc.perform(getCardsReqBuilder())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(id))
                .andExpect(jsonPath("$.content[0].owner").value(OWNER))
                .andExpect(jsonPath("$.content[0].maskedNumber").value(MASK));
    }

    @Test
    void getCards_AsUserForbidden() throws Exception {
        setupUserWithRole("ROLE_USER");
        mockMvc.perform(getCardsReqBuilder())
                .andExpect(status().isForbidden());
    }

    private RequestBuilder getPostRequestBuilder() throws JsonProcessingException {
        YearMonth expire = YearMonth.from(LocalDate.of(2030, 12, 20));
        BankCardCreateDto createDto = new BankCardCreateDto();
        createDto.setOwner(OWNER);
        createDto.setExpire(expire);

        when(bankCardService.createBankCard(any(BankCardCreateDto.class))).thenReturn(bankCardResponseDto);
        return post("/api/cards")
                .with(csrf())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto));
    }

    @Test
    void createBankCard_createsAndReturnsCard() throws Exception {
        mockMvc.perform(getPostRequestBuilder())
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/cards/" + id))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.owner").value(OWNER))
                .andExpect(jsonPath("$.maskedNumber").value(MASK));
    }

    @Test
    void createBankCard_asUser_forbidden() throws Exception {
        setupUserWithRole("ROLE_USER");
        mockMvc.perform(getPostRequestBuilder())
                .andExpect(status().isForbidden());
    }

    private RequestBuilder updateReqBuilder() throws JsonProcessingException {
        BankCardUpdateDto updateDto = new BankCardUpdateDto();
        updateDto.setStatus(CardStatus.BLOCKED);
        bankCardResponseDto.setStatus(updateDto.getStatus());

        when(bankCardService.updateBankCard(eq(id), any(BankCardUpdateDto.class))).thenReturn(bankCardResponseDto);
        return put("/api/cards/{id}", id)
                .with(csrf())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto));
    }
    @Test
    void updateBankCard_updatesAndReturnsCard() throws Exception {
        mockMvc.perform(updateReqBuilder())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.status").value("BLOCKED"));
    }

    @Test
    void updateBankCard_AsUserForbidden() throws Exception{
        setupUserWithRole("ROLE_USER");
        mockMvc.perform(updateReqBuilder())
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteBankCard_deletesAndReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/cards/{id}", id)
                        .header("Authorization", "Bearer " + token)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(bankCardService).deleteBankCard(id);
    }

    @Test
    void deleteBankCard_AsUserForbidden() throws Exception{
        setupUserWithRole("ROLE_USER");
        mockMvc.perform(delete("/api/cards/{id}", id)
                        .header("Authorization", "Bearer " + token)
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

}