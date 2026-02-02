package com.webtrak.user_service.repository;

import com.webtrak.user_service.entity.User;
import com.webtrak.user_service.enums.UserStatus;
import com.webtrak.user_service.enums.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("""
    SELECT u FROM User u
    WHERE (:status IS NULL OR u.status = :status)
      AND (:userType IS NULL OR u.userType = :userType)
""")
    List<User> findUsers(
            @Param("status") UserStatus status,
            @Param("userType") UserType userType
    );

    Optional<User> findByEmail(String email);

    Optional<User> findByEmployeeId(String employeeId);

    boolean existsByEmail(String email);

    boolean existsByEmployeeId(String employeeId);

    boolean existsByEmailAndStatus(String email, UserStatus status);
}
