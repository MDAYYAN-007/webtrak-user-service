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
@RequestMapping("/api/v1/hr/users")
@RequiredArgsConstructor
public class HrUserController {

    private final AdminUserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<Object>> getAllUsers(
            @RequestParam(required = false) UserStatus status,
            @RequestParam(required = false) UserType userType,
            @RequestParam(required = false) String search
    ) {
        List<UserResponse> users = userService
                .getUsers(status, userType, null)
                .stream()
                .map(UserResponse::from)
                .toList();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        new ApiStatus(200, "SUCCESS"),
                        "Users fetched successfully",
                        users
                )
        );
    }

    @GetMapping("/{employeeId}")
    public ResponseEntity<ApiResponse<Object>> getUserByEmployeeId(
            @PathVariable String employeeId
    ) {
        User user = userService.getUserByEmployeeId(employeeId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        new ApiStatus(200, "SUCCESS"),
                        "User fetched successfully",
                        UserResponse.from(user)
                )
        );
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<Object>> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody AdminUpdateUserRequest request
    ) {
        User user = userService.updateUserByAdmin(userId, request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        new ApiStatus(200, "SUCCESS"),
                        "User updated successfully",
                        UserResponse.from(user)
                )
        );
    }
}
