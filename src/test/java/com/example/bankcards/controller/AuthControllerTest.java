package com.example.bankcards.controller;

import com.example.bankcards.dto.LoginRequestDto;
import com.example.bankcards.dto.LoginResponseDto;
import com.example.bankcards.dto.RegisterDto;
import com.example.bankcards.dto.UserResponseDto;
import com.example.bankcards.security.JwtService;
import com.example.bankcards.security.UserDetailsServiceImpl;
import com.example.bankcards.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(AuthControllerTest.TestSecurityConfig.class)
public class AuthControllerTest {
    public static final String PASSWORD = "password123";
    public static final String USERNAME = "+7 927 123 45 12";
    public static final String JWT_TOKEN = "jwt.token.here";
    public static final String NAME = "test";
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private AuthService authService;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private JwtService jwtService;

    private RegisterDto registerDto;
    private UserResponseDto responseDto;

    private LoginRequestDto loginRequestDto;
    private LoginResponseDto loginResponseDto;

    @BeforeEach
    void setUp() {
        registerDto = RegisterDto.builder()
                .username(USERNAME)
                .password(PASSWORD)
                .firstName(NAME)
                .lastName(NAME).build();

        responseDto = UserResponseDto.builder()
                .id(1L)
                .username(USERNAME)
                .firstName(NAME)
                .lastName(NAME).build();


        loginRequestDto = new LoginRequestDto(USERNAME, PASSWORD);
        loginResponseDto = new LoginResponseDto(JWT_TOKEN);
    }

    @Test
    void registerSuccess() throws Exception {


        when(authService.register(any(RegisterDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value(USERNAME))
                .andExpect(jsonPath("$.firstName").value(NAME))
                .andExpect(jsonPath("$.lastName").value(NAME));

    }

    @Test
    void registerInvalidDto() throws Exception {
        registerDto.setPassword(null);
        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").
                        value("password: password is required"));
    }

    @Test
    void loginSuccess() throws Exception {
        when(authService.login(any(LoginRequestDto.class))).thenReturn(loginResponseDto);

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(JWT_TOKEN));
    }

    @Test
    void loginBadCredentials() throws Exception {
        loginRequestDto.setPassword("wrong");
        when(authService.login(any(LoginRequestDto.class)))
                .thenThrow(new BadCredentialsException("bad"));

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Неверный логин или пароль"));
    }

    @Test
    void loginUserDisabled() throws Exception {
        when(authService.login(any(LoginRequestDto.class)))
                .thenThrow(new DisabledException("disabled"));

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Пользователь заблокирован"));
    }

    @Test
    void loginUserLocked() throws Exception {
        when(authService.login(any(LoginRequestDto.class)))
                .thenThrow(new LockedException("locked"));

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Аккаунт заблокирован"));
    }

    @TestConfiguration
    static class TestSecurityConfig {

        @Bean
        SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
            http
                    .csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            return http.build();
        }
    }
}
