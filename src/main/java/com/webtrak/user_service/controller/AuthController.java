package com.webtrak.user_service.controller;

import com.webtrak.user_service.dto.request.ForgotPasswordRequest;
import com.webtrak.user_service.dto.request.LoginRequest;
import com.webtrak.user_service.dto.request.ResetPasswordRequest;
import com.webtrak.user_service.dto.response.ApiResponse;
import com.webtrak.user_service.dto.response.ApiStatus;
import com.webtrak.user_service.service.AuthService;
import com.webtrak.user_service.service.PasswordService;
import com.webtrak.user_service.service.model.LoginResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final PasswordService passwordService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Object>> login(@Valid @RequestBody LoginRequest request) {

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

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Object>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request
    ) {
        passwordService.forgotPassword(request.getEmail());

        return ResponseEntity.ok(
                new ApiResponse<>(
                        new ApiStatus(200, "SUCCESS"),
                        "If the email exists, an OTP has been sent",
                        null
                )
        );
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Object>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request
    ) {
        passwordService.resetPassword(
                request.getEmail(),
                request.getOtp(),
                request.getNewPassword()
        );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        new ApiStatus(200, "SUCCESS"),
                        "Password reset successful",
                        null
                )
        );
    }
}
