package com.example.bankcards.service;

import com.example.bankcards.dto.LoginRequestDto;
import com.example.bankcards.dto.LoginResponseDto;
import com.example.bankcards.dto.RegisterDto;
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
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public void register(RegisterDto dto) {
        User user = mapper.map(dto, User.class);
        Role role = roleRepo.findByName("USER")
                .orElseThrow(() -> new ResourceNotFoundException("Role", "USER"));
        user.getRoles().add(role);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        userRepo.save(user);
        log.info("User registered: {}", dto.getUsername());
    }

    public LoginResponseDto login(LoginRequestDto request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        log.info("User {} successfully authenticated", request.getUsername());
        String token = jwtService.generateToken(request.getUsername());
        return new LoginResponseDto("Bearer " + token);
    }
}
