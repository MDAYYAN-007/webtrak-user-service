package com.webtrak.user_service.dto.response;

import com.webtrak.user_service.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {

    private Long userId;
    private String email;
    private String status;

    public static LoginResponse from(User user) {
        return new LoginResponse(
                user.getId(),
                user.getEmail(),
                user.getStatus().name()
        );
    }
}
