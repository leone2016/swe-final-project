package edu.miu.blog.app.security;


import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String uri = request.getRequestURI();

        System.out.println(">> JwtAuthenticationFilter triggered for URI: {}"+ request.getRequestURI());

        if (uri.startsWith("/swagger-ui") ||
                uri.startsWith("/v2/api-docs") ||
                uri.startsWith("/v3/api-docs") ||
                uri.startsWith("/swagger-resources") ||
                uri.startsWith("/webjars") ||
                uri.equals("/swagger-ui.html")) {
            filterChain.doFilter(request, response);
            return;
        }


        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && (authHeader.startsWith("Bearer ") || authHeader.startsWith("Token "))) {
            String token = authHeader.substring(authHeader.indexOf(" ") + 1); // soporta ambos: Bearer / Token

            if (jwtUtil.isTokenValid(token)) {
                Claims claims = jwtUtil.extractAllClaims(token);

                CurrentUser user = new CurrentUser();
                user.setId(claims.get("id", Integer.class).longValue());
                user.setEmail(claims.get("email", String.class));
                user.setUsername(claims.get("username", String.class));

//                UserContext.set(user);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            System.out.println(">> JwtAuthenticationFilter completed for URI: {}" + request.getRequestURI());
//            UserContext.clear();
        }
    }


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String uri = request.getRequestURI();
        return uri.startsWith("/swagger-ui") ||
                uri.startsWith("/v2/api-docs") ||
                uri.startsWith("/v3/api-docs") ||
                uri.startsWith("/swagger-resources") ||
                uri.startsWith("/webjars") ||
                uri.equals("/swagger-ui.html") ||
                uri.equals("/favicon.ico");
    }

}

