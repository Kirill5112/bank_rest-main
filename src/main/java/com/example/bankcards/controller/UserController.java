package com.example.bankcards.controller;

import com.example.bankcards.dto.BankCardResponseDto;
import com.example.bankcards.dto.CurrentUserDto;
import com.example.bankcards.dto.UserResponseDto;
import com.example.bankcards.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/users")
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public Page<UserResponseDto> getUsers(Pageable pageable) {
        return userService.getUsers(pageable);
    }

    @GetMapping("/current/cards")
    public Page<BankCardResponseDto> getUserCards(
            Principal principal,
            Pageable pageable,
            @RequestParam(required = false) String search) {
        return userService.getUserCards(principal, pageable, search);
    }

    @GetMapping("/current")
    public CurrentUserDto getCurrent(Principal principal) {
        return userService.getCurrent(principal);
    }

    @GetMapping("/{userId}")
    public UserResponseDto getUser(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }

    @PutMapping("/{userId}/toggleEnabled")
    public String toggleEnabled(@PathVariable Long userId) {
        if (userService.toggleEnabledUser(userId))
            return "User unblocked";
        else
            return "User blocked";
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }
}
