package com.webtrak.user_service.controller;

import com.webtrak.user_service.dto.request.ChangePasswordRequest;
import com.webtrak.user_service.dto.response.ApiResponse;
import com.webtrak.user_service.dto.response.ApiStatus;
import com.webtrak.user_service.service.PasswordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserPasswordController {

    private final PasswordService passwordService;

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Object>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        Long userId = (Long) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        passwordService.changePassword(
                userId,
                request.getCurrentPassword(),
                request.getNewPassword()
        );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        new ApiStatus(200, "SUCCESS"),
                        "Password changed successfully",
                        null
                )
        );
    }
}
