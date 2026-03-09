package com.example.bankcards.controller;

import com.example.bankcards.config.SecurityConfig;
import com.example.bankcards.dto.BankCardResponseDto;
import com.example.bankcards.dto.CurrentUserDto;
import com.example.bankcards.dto.UserResponseDto;
import com.example.bankcards.enums.CardStatus;
import com.example.bankcards.security.JwtService;
import com.example.bankcards.security.UserDetailsServiceImpl;
import com.example.bankcards.service.UserService;
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
import java.util.Collection;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private JwtService jwtService;


    private final String token = "test.jwt.token";
    private final String phone = "79122341234";
    private final String maskedNumber = "**** **** **** 1234";
    private final Long id = 10L;
    private final String name = "test";

    private BankCardResponseDto bankCardResponseDto;
    private UserResponseDto userResponseDto;

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
                .owner(phone)
                .maskedNumber(maskedNumber).build();

        userResponseDto = UserResponseDto.builder()
                .id(id)
                .firstName(name)
                .lastName(name)
                .middleName(name)
                .enabled(true)
                .username(phone).build();
    }

    @Test
    void getUserCards_returnsCardsPaginated() throws Exception {
        Page<BankCardResponseDto> pageCards = new PageImpl<>(List.of(bankCardResponseDto));

        when(userService.getUserCards(any(Principal.class), any(Pageable.class), anyString()))
                .thenReturn(pageCards);
        when(userService.getUserCards(any(Principal.class), any(Pageable.class), eq(null)))
                .thenReturn(pageCards);

        mockMvc.perform(get("/api/users/current/cards")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(id))
                .andExpect(jsonPath("$.content[0].owner").value(phone))
                .andExpect(jsonPath("$.content[0].maskedNumber").value(maskedNumber));
    }

    @Test
    void blockCurrentCard_blocksCurCardAndReturnUser() throws Exception {
        bankCardResponseDto.setStatus(CardStatus.BLOCKED);

        when(userService.blockCurrentCard(any(Principal.class), eq(id))).thenReturn(bankCardResponseDto);

        mockMvc.perform(put("/api/users/current/{cardId}", id)
                        .with(csrf())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.status").value("BLOCKED"))
                .andExpect(jsonPath("$.owner").value(phone))
                .andExpect(jsonPath("$.maskedNumber").value(maskedNumber));
    }

    @Test
    void getCurrent_returnCurrentUser() throws Exception {
        BigDecimal fullBalance = new BigDecimal(100);
        CurrentUserDto curUser = CurrentUserDto.builder()
                .username(phone)
                .firstName(name)
                .middleName(name)
                .lastName(name)
                .fullBalance(fullBalance).build();

        when(userService.getCurrent(any(Principal.class))).thenReturn(curUser);

        mockMvc.perform(get("/api/users/current")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(phone))
                .andExpect(jsonPath("$.firstName").value(name))
                .andExpect(jsonPath("$.lastName").value(name))
                .andExpect(jsonPath("$.middleName").value(name))
                .andExpect(jsonPath("$.fullBalance").value(fullBalance));
    }

    @Test
    void getUsers_returnUsersPaginated() throws Exception {
        Page<UserResponseDto> pageUsers = new PageImpl<>(List.of(userResponseDto));
        when(userService.getUsers(any(Pageable.class))).thenReturn(pageUsers);
        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].username").value(phone))
                .andExpect(jsonPath("$.content[0].firstName").value(name))
                .andExpect(jsonPath("$.content[0].lastName").value(name))
                .andExpect(jsonPath("$.content[0].middleName").value(name))
                .andExpect(jsonPath("$.content[0].id").value(id))
                .andExpect(jsonPath("$.content[0].enabled").value(true));
    }

    @Test
    void getUsers_AsUserForbidden() throws Exception {
        setupUserWithRole("ROLE_USER");
        Page<UserResponseDto> pageUsers = new PageImpl<>(List.of(userResponseDto));
        when(userService.getUsers(any(Pageable.class))).thenReturn(pageUsers);
        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void getUser_ReturnUser() throws Exception {
        when(userService.getUserById(id)).thenReturn(userResponseDto);
        mockMvc.perform(get("/api/users/{userId}", id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(phone))
                .andExpect(jsonPath("$.firstName").value(name))
                .andExpect(jsonPath("$.lastName").value(name))
                .andExpect(jsonPath("$.middleName").value(name))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.enabled").value(true));
    }

    @Test
    void getUser_asRoleUserForbidden() throws Exception {
        setupUserWithRole("ROLE_USER");
        when(userService.getUserById(anyLong())).thenReturn(userResponseDto);
        mockMvc.perform(get("/api/users/{userId}", id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void toggleEnabled_togglesEnabledAndReturnUser() throws Exception {
        userResponseDto.setEnabled(!userResponseDto.isEnabled());
        when(userService.toggleEnabledUser(id)).thenReturn(userResponseDto);
        mockMvc.perform(put("/api/users/{userId}/toggleEnabled", id)
                        .with(csrf())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(phone))
                .andExpect(jsonPath("$.firstName").value(name))
                .andExpect(jsonPath("$.lastName").value(name))
                .andExpect(jsonPath("$.middleName").value(name))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.enabled").value(userResponseDto.isEnabled()));
    }

    @Test
    void toggleEnabled_AsRoleUserForbidden() throws Exception {
        setupUserWithRole("ROLE_USER");
        when(userService.toggleEnabledUser(anyLong())).thenReturn(userResponseDto);
        mockMvc.perform(put("/api/users/{userId}/toggleEnabled", id)
                        .with(csrf())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteUser_deletesUserAndReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/users/{userId}", id)
                .with(csrf())
                .header("Authorization", "Bearer "+ token))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUser_AsRoleUserForbidden() throws Exception{
        setupUserWithRole("ROLE_USER");
        mockMvc.perform(delete("/api/users/{userId}", id)
                        .with(csrf())
                        .header("Authorization", "Bearer "+ token))
                .andExpect(status().isForbidden());
    }
}
