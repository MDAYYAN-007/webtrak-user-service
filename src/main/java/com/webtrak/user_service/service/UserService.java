package com.webtrak.user_service.service;

import com.webtrak.user_service.dto.request.UpdateProfileRequest;
import com.webtrak.user_service.entity.User;
import com.webtrak.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getMyProfile(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public User updateMyProfile(Long userId, UpdateProfileRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (request.getName() != null) {
            user.setName(request.getName().trim());
        }

        if (request.getPhone() != null) {
            user.setPhone(request.getPhone().trim());
        }

        return userRepository.save(user);
    }
}
