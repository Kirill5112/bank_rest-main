package com.example.bankcards.service;

import com.example.bankcards.dto.LoginRequestDto;
import com.example.bankcards.dto.LoginResponseDto;
import com.example.bankcards.dto.RegisterDto;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.IncorrectPhoneNumberException;
import com.example.bankcards.repository.RoleRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static com.example.bankcards.util.PhoneNormalizer.normalizePhone;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;
    @Mock
    private UserRepository userRepo;
    @Mock
    private RoleRepository roleRepo;
    @Mock
    private ModelMapper mapper;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private final String correctPhone = "+7(999)123-45-67";
    private final String normalizedPhone = normalizePhone(correctPhone);
    private RegisterDto registerDto;
    private Role userRole;
    private User mappedUser;
    private LoginRequestDto loginRequestDto;
    private final String incorrectPhone = "invalid";

    @BeforeEach
    void setUp() {
        registerDto = new RegisterDto();
        registerDto.setUsername(correctPhone);
        registerDto.setPassword("rawPass123");
        userRole = new Role();
        userRole.setName("USER");
        mappedUser = new User();
        loginRequestDto = new LoginRequestDto();
        loginRequestDto.setPassword("pass123");
    }

    @Test
    void register_AssignsUserRole_AndEncodesPassword() {
        when(mapper.map(registerDto, User.class)).thenReturn(mappedUser);
        when(roleRepo.findByName("USER")).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPass");

        authService.register(registerDto);

        verify(mapper).map(registerDto, User.class);
        verify(passwordEncoder).encode(registerDto.getPassword());
        verify(userRepo).save(argThat(user ->
                user.getRoles().contains(userRole) &&
                user.getPassword().equals("encodedPass")
        ));
    }

    @Test
    void register_ThrowsIncorrectPhoneNumberException() {
        when(mapper.map(registerDto, User.class)).thenReturn(mappedUser);
        registerDto.setUsername(incorrectPhone);
        assertThrows(IncorrectPhoneNumberException.class,
                () -> authService.register(registerDto));
    }

    @Test
    void login_ThrowsIncorrectPhoneNumberException() {
        loginRequestDto.setUsername(incorrectPhone);
        assertThrows(IncorrectPhoneNumberException.class,
                () -> authService.login(loginRequestDto));
    }

    @Test
    void login_Authenticate_AndGenerateToken_fromNormalizedUsername() {
        loginRequestDto.setUsername(correctPhone);

        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(jwtService.generateToken(anyString())).thenReturn("jwt.token");

        LoginResponseDto result = authService.login(loginRequestDto);

        assertEquals("Bearer jwt.token", result.getToken());
        verify(authenticationManager).authenticate(argThat(
                auth ->
                        auth.getPrincipal().equals(normalizedPhone) &&
                        auth.getCredentials().equals("pass123")
        ));
        verify(jwtService).generateToken(argThat(s ->
                s.equals(normalizedPhone)));
    }
}
