package com.webtrak.user_service.service.model;

import com.webtrak.user_service.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class LoginResult {
    private String accessToken;
    private User user;
    private List<String> roles;
}
