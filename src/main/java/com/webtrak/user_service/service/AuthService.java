package com.webtrak.user_service.service;

import com.webtrak.user_service.entity.User;
import com.webtrak.user_service.enums.UserStatus;
import com.webtrak.user_service.repository.UserRepository;
import com.webtrak.user_service.repository.UserRoleRepository;
import com.webtrak.user_service.security.JwtUtil;
import com.webtrak.user_service.service.model.LoginResult;
import lombok.RequiredArgsConstructor;
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

    public User authenticate(String email, String rawPassword) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new RuntimeException("User is inactive");
        }

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
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
