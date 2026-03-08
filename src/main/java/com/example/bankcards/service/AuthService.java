package com.example.bankcards.service;

import com.example.bankcards.dto.LoginRequestDto;
import com.example.bankcards.dto.LoginResponseDto;
import com.example.bankcards.dto.RegisterDto;
import com.example.bankcards.dto.UserResponseDto;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.repository.RoleRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.example.bankcards.util.PhoneNormalizer.normalizePhone;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final ModelMapper mapper;
    private final PasswordEncoder passwordEncoder;

    public UserResponseDto register(RegisterDto dto) {
        User user = mapper.map(dto, User.class);
        user.setUsername(dto.getUsername());
        Role role = roleRepo.findByName("USER")
                .orElseThrow(() -> new ResourceNotFoundException("Role", "USER"));
        user.getRoles().add(role);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user = userRepo.save(user);
        log.info("User registered: {}", dto.getUsername());
        return mapper.map(user, UserResponseDto.class);
    }

    public LoginResponseDto login(LoginRequestDto request) {
        String username = normalizePhone(request.getUsername());
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, request.getPassword())
        );
        log.info("User {} successfully authenticated", username);
        String token = jwtService.generateToken(username);
        return new LoginResponseDto("Bearer " + token);
    }
}
