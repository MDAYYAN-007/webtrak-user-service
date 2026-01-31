package com.webtrak.user_service.controller;

import com.webtrak.user_service.dto.request.CreateUserRequest;
import com.webtrak.user_service.dto.response.ApiResponse;
import com.webtrak.user_service.dto.response.ApiStatus;
import com.webtrak.user_service.dto.response.UserResponse;
import com.webtrak.user_service.entity.User;
import com.webtrak.user_service.enums.UserStatus;
import com.webtrak.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<Object>> createUser(
            @RequestBody CreateUserRequest request
    ) {

        User user = userService.createUser(
                request.getEmployeeId(),
                request.getEmail(),
                request.getUserType()
        );

        ApiResponse<Object> response = new ApiResponse<>(
                new ApiStatus(200, "SUCCESS"),
                "User created successfully",
                UserResponse.from(user)
        );

        return ResponseEntity.ok(response);
    }


    @GetMapping
    public ResponseEntity<ApiResponse<Object>> getAllUsers() {

        List<UserResponse> users = userService.getAllUsers()
                .stream()
                .map(UserResponse::from)
                .collect(Collectors.toList());

        ApiResponse<Object> response = new ApiResponse<>(
                new ApiStatus(200, "SUCCESS"),
                "Users fetched successfully",
                users
        );

        return ResponseEntity.ok(response);
    }


    @PatchMapping("/{userId}/status")
    public ResponseEntity<ApiResponse<Object>> updateStatus(
            @PathVariable Long userId,
            @RequestParam UserStatus status
    ) {

        User user = userService.updateUserStatus(userId, status);

        ApiResponse<Object> response = new ApiResponse<>(
                new ApiStatus(200, "SUCCESS"),
                "User status updated successfully",
                UserResponse.from(user)
        );

        return ResponseEntity.ok(response);
    }

}
