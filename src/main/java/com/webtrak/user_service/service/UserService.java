package com.webtrak.user_service.service;

import com.webtrak.user_service.entity.User;
import com.webtrak.user_service.entity.UserRole;
import com.webtrak.user_service.enums.Role;
import com.webtrak.user_service.enums.UserStatus;
import com.webtrak.user_service.enums.UserType;
import com.webtrak.user_service.repository.UserRepository;
import com.webtrak.user_service.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    public User createUser(String employeeId, String email, UserType userType) {

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        if (userRepository.existsByEmployeeId(employeeId)) {
            throw new RuntimeException("Employee ID already exists");
        }

        String tempPassword = "Temp@123"; // later generate properly

        User user = User.builder()
                .employeeId(employeeId)
                .email(email)
                .password(passwordEncoder.encode(tempPassword))
                .userType(userType)
                .status(UserStatus.ACTIVE)
                .build();

        User savedUser = userRepository.save(user);

        UserRole defaultRole = UserRole.builder()
                .user(savedUser)
                .role(Role.EMPLOYEE)
                .build();

        userRoleRepository.save(defaultRole);

        return savedUser;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUserStatus(Long userId, UserStatus status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setStatus(status);
        return userRepository.save(user);
    }
}
