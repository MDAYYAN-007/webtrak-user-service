package com.webtrak.user_service.config.security;

import com.webtrak.user_service.security.JwtAuthFilter;
import com.webtrak.user_service.security.JwtUtil;
import com.webtrak.user_service.security.SecurityErrorHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// We use configuration so that we can have @Beans inside it
@Configuration
// Enables spring security here
@EnableWebSecurity
// Alternate for constructor
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    // We create classes using @Bean as we don't own the interface and they belong to spring or any library
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, SecurityErrorHandler securityErrorHandler) throws Exception {

        http
                // csrf disables malicious websites from doing something in our website
                .csrf(csrf -> csrf.disable())
                // Every request must carry JWT token
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // Custom handling for auth & access errors
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(securityErrorHandler)
                        .accessDeniedHandler(securityErrorHandler)
                )
                // Authorization rules
                .authorizeHttpRequests(auth -> auth
                        // No auth req for public endpoints
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/api/v1/auth/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/api/v1/auth/login",
                                "/api/v1/auth/forgot-password",
                                "/api/v1/auth/reset-password",
                                "/internal/**",
                                "/actuator/**"
                        ).permitAll()
                        // Only ADMIN can access admin APIs
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")

                        // HR + ADMIN
                        .requestMatchers("/api/v1/hr/**").hasAnyRole("HR", "ADMIN")
                        .anyRequest().authenticated()
                )
                // Add JWT filter before Spring’s username/password filter
                .addFilterBefore(
                        new JwtAuthFilter(jwtUtil),
                        UsernamePasswordAuthenticationFilter.class
                )
                // Diable def logins and stuff
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}