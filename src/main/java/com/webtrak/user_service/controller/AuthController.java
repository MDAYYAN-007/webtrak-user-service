package com.webtrak.user_service.controller;

import com.webtrak.user_service.dto.request.LoginRequest;
import com.webtrak.user_service.dto.response.ApiResponse;
import com.webtrak.user_service.dto.response.ApiStatus;
import com.webtrak.user_service.service.AuthService;
import com.webtrak.user_service.service.model.LoginResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Object>> login(@RequestBody LoginRequest request) {

        LoginResult result = authService.login(
                request.getEmail(),
                request.getPassword()
        );

        ApiStatus status = new ApiStatus(200, "SUCCESS");

        ApiResponse<Object> response = new ApiResponse<>(
                status,
                "Login successful",
                Map.of(
                        "accessToken", result.getAccessToken(),
                        "tokenType", "Bearer",
                        "user", Map.of(
                                "userId", result.getUser().getId(),
                                "email", result.getUser().getEmail(),
                                "status", result.getUser().getStatus().name()
                        )
                )
        );

        return ResponseEntity.ok(response);
    }
}
