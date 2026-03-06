package com.example.bankcards.util;

import com.example.bankcards.exception.AuthenticationMismatchException;
import org.springframework.security.core.Authentication;

import java.security.Principal;

public class SessionUserHelper {

    public static String getSessionUsername(Principal principal) {
        if (!((Authentication) principal).isAuthenticated())
            throw new AuthenticationMismatchException();
        return principal.getName();
    }
}
