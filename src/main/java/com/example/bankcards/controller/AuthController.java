package com.example.bankcards.controller;

import com.example.bankcards.dto.UserLoginDto;
import com.example.bankcards.dto.UserRegisterDto;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.repository.RoleRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.JwtService;
import com.example.bankcards.util.PhoneNormalizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepo;
    private final ModelMapper mapper;
    private final RoleRepository roleRepo;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public void register(@RequestBody UserRegisterDto dto){
        User user = mapper.map(dto, User.class);
        Role role = roleRepo.findByName("USER")
                .orElseThrow(() -> new ResourceNotFoundException("Role", "USER"));
        user.getRoles().add(role);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        userRepo.save(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDto request) {
        String username = PhoneNormalizer.normalizePhone(request.getUsername());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, request.getPassword())
            );
            log.info("Security good successful");
            String token = jwtService.generateToken(username);
            return ResponseEntity.ok().body(Map.of("token", "Bearer " + token));
        } catch (Exception e) {
            log.error("Security authorization failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
