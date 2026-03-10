package com.example.bankcards.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

import static org.springframework.security.core.context.SecurityContextHolder.createEmptyContext;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String BEARER_PREFIX = "Bearer ";
    public static final String HEADER_NAME = "Authorization";
    private static final List<String> OPEN_ROUTES = List.of(
            "/api/login", "/api/register", "/login", "/register");

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        var authHeader = request.getHeader(HEADER_NAME);
        if (isOpenRoutes(request) || authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(BEARER_PREFIX.length());
        String username = jwtService.extractUsername(jwt);

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!username.isEmpty() && authentication == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtService.validateToken(jwt)) {
                configureSecurityContextHolder(request, userDetails);
            } else throw new ServletException("Token is not valid");
        }
        filterChain.doFilter(request, response);
    }

    private static void configureSecurityContextHolder
            (HttpServletRequest request, UserDetails userDetails) {
        SecurityContext context = createEmptyContext();

        var authToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        context.setAuthentication(authToken);
        SecurityContextHolder.setContext(context);
    }

    private static boolean isOpenRoutes(HttpServletRequest request) {
        String path = request.getRequestURI();
        return OPEN_ROUTES.stream().anyMatch(path::startsWith)
               && request.getMethod().equals("GET");
    }
}
