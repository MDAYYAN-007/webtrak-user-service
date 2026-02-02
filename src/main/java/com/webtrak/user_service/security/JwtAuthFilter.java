package com.webtrak.user_service.security;

import io.jsonwebtoken.Claims;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;


public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {

        String path = request.getRequestURI();

        if (path.startsWith("/api/v1/auth")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")) {

            chain.doFilter(request, response);
            return;
        }

        String auth = request.getHeader("Authorization");

        if (auth != null && auth.startsWith("Bearer ")) {
            try {
                Claims claims = jwtUtil.validate(auth.substring(7));

                Long userId = claims.get("userId", Long.class);
                List<String> roles = claims.get("roles", List.class);

                var authorities = roles.stream()
                        .map(r -> "ROLE_" + r)
                        .map(SimpleGrantedAuthority::new)
                        .toList();

                var authentication =
                        new UsernamePasswordAuthenticationToken(userId, null, authorities);

                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception ignored) {
                SecurityContextHolder.clearContext();
            }
        }

        chain.doFilter(request, response);
    }
}
