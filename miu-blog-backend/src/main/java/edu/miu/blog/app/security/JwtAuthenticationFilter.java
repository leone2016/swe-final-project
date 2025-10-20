package edu.miu.blog.app.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER = "Bearer ";
    private static final String TOKEN  = "Token ";

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        log.debug("Processing request: {} {}", request.getMethod(), request.getRequestURI());

        if (authHeader != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            log.debug("Found Authorization header");
            String token = resolveToken(authHeader);

            if (token != null) {
                try {
                    log.debug("Validating JWT token");
                    Claims claims = jwtUtil.extractAllClaims(token);

                    CurrentUser user = new CurrentUser();
                    user.setId(claims.get("id", Integer.class).longValue());
                    user.setEmail(claims.get("email", String.class));
                    user.setUsername(claims.get("username", String.class));

                    log.debug("JWT token validated for user: {}", user.getUsername());
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } catch (JwtException ex) {
                    log.warn("Invalid or expired JWT token: {}", ex.getMessage());
                   new RuntimeException("Invalid or expired JWT token", ex);
                }
            } else {
                log.debug("No valid token found in Authorization header");
            }
        } else {
            log.debug("No Authorization header or user already authenticated");
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(String header) {
        log.debug("Resolving token from header");
        if (header.startsWith(BEARER)) {
            log.debug("Found Bearer token");
            return header.substring(BEARER.length()).trim();
        }
        if (header.startsWith(TOKEN)) {
            log.debug("Found Token header");
            return header.substring(TOKEN.length()).trim();
        }
        log.debug("No valid token prefix found");
        return null;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        boolean shouldNotFilter = uri.startsWith("/swagger-ui") ||
                uri.startsWith("/v3/api-docs") ||
                uri.startsWith("/v2/api-docs") ||
                uri.startsWith("/swagger-resources") ||
                uri.startsWith("/webjars") ||
                uri.equals("/swagger-ui.html") ||
                uri.equals("/favicon.ico") ||
                uri.startsWith("/auth/") ||
                uri.startsWith("/actuator/");
        
        if (shouldNotFilter) {
            log.debug("Skipping JWT filter for URI: {}", uri);
        }
        return shouldNotFilter;
    }
}

