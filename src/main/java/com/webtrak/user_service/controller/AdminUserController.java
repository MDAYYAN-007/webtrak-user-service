package com.webtrak.user_service.controller;

import com.webtrak.user_service.dto.request.AdminUpdateUserRequest;
import com.webtrak.user_service.dto.request.CreateUserRequest;
import com.webtrak.user_service.dto.response.ApiResponse;
import com.webtrak.user_service.dto.response.ApiStatus;
import com.webtrak.user_service.dto.response.BulkUploadResult;
import com.webtrak.user_service.dto.response.UserResponse;
import com.webtrak.user_service.entity.User;
import com.webtrak.user_service.entity.UserRole;
import com.webtrak.user_service.enums.Role;
import com.webtrak.user_service.enums.UserStatus;
import com.webtrak.user_service.enums.UserType;
import com.webtrak.user_service.repository.UserRepository;
import com.webtrak.user_service.repository.UserRoleRepository;
import com.webtrak.user_service.service.AdminUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @PostMapping
    public ResponseEntity<ApiResponse<Object>> createUser(
            @Valid @RequestBody CreateUserRequest request
    ) {
        User user = adminUserService.createUser(
                request.getEmployeeId(),
                request.getEmail(),
                request.getUserType()
        );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        new ApiStatus(200, "SUCCESS"),
                        "User created successfully",
                        UserResponse.from(user)
                )
        );
    }

    @PostMapping("/bulk-upload")
    public ResponseEntity<ApiResponse<Object>> bulkUploadUsers(
            @RequestParam("file") MultipartFile file
    ) {
        BulkUploadResult result = adminUserService.bulkUploadUsers(file);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        new ApiStatus(200, "SUCCESS"),
                        "Bulk upload completed",
                        result
                )
        );
    }

    @PostMapping("/{userId}/roles")
    public ResponseEntity<ApiResponse<Object>> assignRole(
            @PathVariable Long userId,
            @RequestBody Map<String, Role> request
    ) {
        adminUserService.assignRole(userId, request.get("role"));

        return ResponseEntity.ok(
                new ApiResponse<>(
                        new ApiStatus(200, "SUCCESS"),
                        "Role updated successfully",
                        null
                )
        );
    }

    @PatchMapping("/{userId}/status")
    public ResponseEntity<ApiResponse<Object>> updateStatus(
            @PathVariable Long userId,
            @RequestParam UserStatus status
    ) {
        User user = adminUserService.updateUserStatus(userId, status);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        new ApiStatus(200, "SUCCESS"),
                        "User status updated successfully",
                        UserResponse.from(user)
                )
        );
    }
}
