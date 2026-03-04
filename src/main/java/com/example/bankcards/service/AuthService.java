package com.example.bankcards.service;

import com.example.bankcards.dto.UserRegisterDto;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.repository.RoleRepository;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepo;
    private final ModelMapper mapper;
    private final RoleRepository roleRepo;
    private final PasswordEncoder passwordEncoder;


    public void register(UserRegisterDto dto) {
        User user = mapper.map(dto, User.class);
        Role role = roleRepo.findByName("USER")
                .orElseThrow(() -> new ResourceNotFoundException("Role", "USER"));
        user.getRoles().add(role);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        userRepo.save(user);
    }
}
