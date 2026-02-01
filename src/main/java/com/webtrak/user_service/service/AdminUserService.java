package com.webtrak.user_service.service;

import com.webtrak.user_service.dto.response.BulkUploadResult;
import com.webtrak.user_service.entity.User;
import com.webtrak.user_service.entity.UserRole;
import com.webtrak.user_service.enums.Role;
import com.webtrak.user_service.enums.UserStatus;
import com.webtrak.user_service.enums.UserType;
import com.webtrak.user_service.repository.UserRepository;
import com.webtrak.user_service.repository.UserRoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d).{8,}$";


    public User createUser(String employeeId, String email, UserType userType) {

        String normalizedEmail = email.trim().toLowerCase();

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException("Email already exists");
        }

        if (userRepository.existsByEmployeeId(employeeId)) {
            throw new IllegalArgumentException("Employee ID already exists");
        }

        User user = User.builder()
                .employeeId(employeeId)
                .email(normalizedEmail)
                .userType(userType)
                .status(UserStatus.PENDING)
                .password(null)
                .build();

        User savedUser = userRepository.save(user);

        userRoleRepository.save(
                UserRole.builder()
                        .user(savedUser)
                        .role(Role.EMPLOYEE)
                        .build()
        );

        return savedUser;
    }

    public List<User> getUsers(
            UserStatus status,
            UserType userType,
            Role role
    ) {
        // Step 1: filter by status + userType
        List<User> users = userRepository.findUsers(status, userType);

        // Step 2: if role filter not requested → done
        if (role == null) {
            return users;
        }

        // Step 3: filter by role
        List<Long> userIdsWithRole = userRoleRepository.findUserIdsByRole(role);

        return users.stream()
                .filter(u -> userIdsWithRole.contains(u.getId()))
                .toList();
    }

    public User updateUserStatus(Long userId, UserStatus status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setStatus(status);
        return userRepository.save(user);
    }

    public User getUserByEmployeeId(String employeeId) {

        return userRepository.findByEmployeeId(employeeId)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found with employeeId: " + employeeId)
                );
    }

    @Transactional
    public void assignRole(Long userId, Role newRole) {

        if (newRole == Role.ADMIN) {
            throw new IllegalArgumentException("ADMIN role cannot be assigned");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        boolean isAdmin = userRoleRepository
                .existsByUser_IdAndRole(userId, Role.ADMIN);

        if (isAdmin) {
            throw new IllegalArgumentException(
                    "Roles of ADMIN users cannot be modified"
            );
        }

        userRoleRepository.deleteByUser_Id(userId);

        UserRole role = UserRole.builder()
                .user(user)
                .role(newRole)
                .build();

        userRoleRepository.save(role);
    }

    @Transactional
    public BulkUploadResult bulkUploadUsers(MultipartFile file) {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("CSV file is empty");
        }

        int totalRows = 0;
        int successCount = 0;
        List<String> errors = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream())
        )) {
            String line;
            boolean headerSkipped = false;

            while ((line = reader.readLine()) != null) {

                // skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }

                totalRows++;

                // skip header
                if (!headerSkipped) {
                    headerSkipped = true;
                    continue;
                }

                try {
                    String[] columns = line.split(",");

                    if (columns.length != 3) {
                        throw new IllegalArgumentException("Invalid column count");
                    }

                    String employeeId = columns[0].trim();
                    String email = columns[1].trim().toLowerCase();
                    String userTypeRaw = columns[2].trim();

                    if (employeeId.isEmpty()) {
                        throw new IllegalArgumentException("Employee ID is required");
                    }

                    if (email.isEmpty()) {
                        throw new IllegalArgumentException("Email is required");
                    }

                    if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                        throw new IllegalArgumentException("Invalid email format");
                    }

                    UserType userType;
                    try {
                        userType = UserType.valueOf(userTypeRaw);
                    } catch (Exception ex) {
                        throw new IllegalArgumentException("Invalid userType");
                    }

                    createUserInternal(employeeId, email, userType);
                    successCount++;

                } catch (Exception e) {
                    errors.add("Row " + totalRows + ": " + e.getMessage());
                }
            }

        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to process CSV file");
        }

        return new BulkUploadResult(
                totalRows - 1,
                successCount,
                errors.size(),
                errors
        );
    }

    private void createUserInternal(
            String employeeId,
            String email,
            UserType userType
    ) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }

        if (userRepository.existsByEmployeeId(employeeId)) {
            throw new IllegalArgumentException("Employee ID already exists");
        }

        User user = User.builder()
                .employeeId(employeeId)
                .email(email)
                .userType(userType)
                .status(UserStatus.PENDING)
                .password(null)
                .build();

        User savedUser = userRepository.save(user);

        userRoleRepository.save(
                UserRole.builder()
                        .user(savedUser)
                        .role(Role.EMPLOYEE)
                        .build()
        );
    }

}
