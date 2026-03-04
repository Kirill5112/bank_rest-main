package com.example.bankcards.controller;

import com.example.bankcards.dto.BankCardResponseDto;
import com.example.bankcards.dto.UserResponseDto;
import com.example.bankcards.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{userId}/cards")
    public List<BankCardResponseDto> getUserCards(@PathVariable Long userId) {
        return userService.getUserCards(userId);
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
