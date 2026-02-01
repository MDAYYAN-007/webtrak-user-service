package com.webtrak.user_service.dto.request;

import lombok.Getter;

@Getter
public class ResetPasswordRequest {
    private String email;
    private String otp;
    private String newPassword;
}
