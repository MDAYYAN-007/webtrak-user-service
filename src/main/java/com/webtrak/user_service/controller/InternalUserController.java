package com.webtrak.user_service.controller;

import com.webtrak.user_service.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/users")
@RequiredArgsConstructor
public class InternalUserController {

    private final UserRepository userRepository;

    @GetMapping("/{userId}/employment-type")
    public String getEmploymentType(@PathVariable Long userId) {

        return userRepository.findById(userId)
                .map(user -> user.getUserType().name())
                .orElseThrow(() ->
                        new EntityNotFoundException("User not found: " + userId));
    }
}
