package com.webtrak.user_service.service;

import com.webtrak.user_service.entity.User;
import com.webtrak.user_service.enums.UserStatus;
import com.webtrak.user_service.exception.AccountNotActivatedException;
import com.webtrak.user_service.repository.UserRepository;
import com.webtrak.user_service.repository.UserRoleRepository;
import com.webtrak.user_service.security.JwtUtil;
import com.webtrak.user_service.service.model.LoginResult;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    private static final String AUTH_ERROR_MSG = "Invalid email or password";

    public User authenticate(String email, String rawPassword) {

        String normalizedEmail = email == null
                ? null
                : email.trim().toLowerCase();

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new BadCredentialsException(AUTH_ERROR_MSG));

        if (user.getStatus() == UserStatus.PENDING) {
            throw new AccountNotActivatedException(
                    "Account not activated. Please reset your password to activate your account."
            );
        }

        if (user.getStatus() == UserStatus.INACTIVE) {
            throw new BadCredentialsException(AUTH_ERROR_MSG);
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new BadCredentialsException(AUTH_ERROR_MSG);
        }

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new BadCredentialsException(AUTH_ERROR_MSG);
        }

        return user;
    }

    public LoginResult login(String email, String rawPassword) {

        User user = authenticate(email, rawPassword);

        List<String> roles = userRoleRepository.findByUser_Id(user.getId())
                .stream()
                .map(r -> r.getRole().name())
                .toList();

        String token = jwtUtil.generateToken(
                user.getId(),
                user.getEmail(),
                roles
        );

        return new LoginResult(token, user, roles);
    }
}
