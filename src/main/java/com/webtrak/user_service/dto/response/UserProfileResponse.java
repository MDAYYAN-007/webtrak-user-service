package com.webtrak.user_service.dto.response;

import com.webtrak.user_service.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserProfileResponse {

    private Long id;
    private String employeeId;
    private String email;
    private String name;
    private String phone;
    private String profilePicUrl;
    private String userType;
    private String status;

    public static UserProfileResponse from(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .employeeId(user.getEmployeeId())
                .email(user.getEmail())
                .name(user.getName())
                .phone(user.getPhone())
                .userType(user.getUserType().name())
                .status(user.getStatus().name())
                .build();
    }
}
