package com.webtrak.user_service.controller;

import com.webtrak.user_service.dto.request.CreateUserRequest;
import com.webtrak.user_service.entity.User;
import com.webtrak.user_service.enums.UserStatus;
import com.webtrak.user_service.enums.UserType;
import com.webtrak.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest request) {

        User user = userService.createUser(
                request.getEmployeeId(),
                request.getEmail(),
                request.getUserType()
        );

        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PatchMapping("/{userId}/status")
    public ResponseEntity<User> updateStatus(
            @PathVariable Long userId,
            @RequestParam UserStatus status
    ) {
        return ResponseEntity.ok(
                userService.updateUserStatus(userId, status)
        );
    }
}
