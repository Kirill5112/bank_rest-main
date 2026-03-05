package com.example.bankcards.security;

import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = new User();
        user.setUsername(username);
        //effectively final
        String finalUsername = user.getUsername();
        user = userRepo.findByUsername(finalUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User " + finalUsername + " not found"));

        Set<GrantedAuthority> authorities = user.getRoles()
                .stream().map(
                        role -> new SimpleGrantedAuthority(
                                "ROLE_" + role.getName())
                )
                .collect(Collectors.toSet());
        return new org.springframework.security.core.userdetails.User(
                finalUsername,
                user.getPassword(),
                user.isEnabled(),
                true, true, true,
                authorities
        );
    }
}

