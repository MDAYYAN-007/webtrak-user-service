package com.webtrak.user_service.controller;

import com.webtrak.user_service.dto.request.UpdateProfileRequest;
import com.webtrak.user_service.dto.response.ApiResponse;
import com.webtrak.user_service.dto.response.ApiStatus;
import com.webtrak.user_service.dto.response.UserProfileResponse;
import com.webtrak.user_service.entity.User;
import com.webtrak.user_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/users/profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<Object>> getMyProfile(Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();

        User user = userService.getMyProfile(userId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        new ApiStatus(200, "SUCCESS"),
                        "Profile fetched successfully",
                        UserProfileResponse.from(user)
                )
        );
    }

    @PutMapping
    public ResponseEntity<ApiResponse<Object>> updateMyProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        Long userId = (Long) authentication.getPrincipal();

        User user = userService.updateMyProfile(userId, request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        new ApiStatus(200, "SUCCESS"),
                        "Profile updated successfully",
                        UserProfileResponse.from(user)
                )
        );
    }

}
