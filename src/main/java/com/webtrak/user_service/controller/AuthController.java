package com.webtrak.user_service.controller;

import com.webtrak.user_service.dto.request.LoginRequest;
import com.webtrak.user_service.dto.response.LoginResponse;
import com.webtrak.user_service.entity.User;
import com.webtrak.user_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        User user = authService.authenticate(
                request.getEmail(),
                request.getPassword()
        );

        return ResponseEntity.ok(LoginResponse.from(user));
    }
}
