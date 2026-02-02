package com.webtrak.user_service.dto.response;

import com.webtrak.user_service.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String employeeId;
    private String email;
    private String name;
    private String phone;
    private String userType;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmployeeId(),
                user.getEmail(),
                user.getName(),
                user.getPhone(),
                user.getUserType().name(),
                user.getStatus().name(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
