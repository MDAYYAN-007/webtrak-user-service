package com.webtrak.user_service.dto.request;

import com.webtrak.user_service.enums.UserType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserRequest {
    private String employeeId;
    private String email;
    private UserType userType;
}
