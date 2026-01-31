package com.webtrak.user_service.repository;

import com.webtrak.user_service.entity.User;
import com.webtrak.user_service.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByEmployeeId(String employeeId);

    boolean existsByEmail(String email);

    boolean existsByEmployeeId(String employeeId);

    boolean existsByEmailAndStatus(String email, UserStatus status);
}
