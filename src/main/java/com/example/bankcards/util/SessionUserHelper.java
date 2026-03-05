package com.example.bankcards.util;

import com.example.bankcards.entity.User;
import com.example.bankcards.exception.AuthenticationMismatchException;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
public class SessionUserHelper {
    private static UserRepository userRepo;

    @Autowired
    public void setUserRepo(UserRepository userRepo) {
        SessionUserHelper.userRepo = userRepo;
    }

    public static User getSessionUser(Principal principal) {
        if (!((Authentication) principal).isAuthenticated())
            throw new AuthenticationMismatchException();
        String username = principal.getName();
        return userRepo.findByUsername(username).orElseThrow(() ->
                new ResourceNotFoundException("User", username));
    }
}
