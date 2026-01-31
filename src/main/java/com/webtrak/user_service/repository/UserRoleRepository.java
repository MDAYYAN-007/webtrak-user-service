package com.webtrak.user_service.repository;

import com.webtrak.user_service.entity.UserRole;
import com.webtrak.user_service.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    List<UserRole> findByUser_Id(Long userId);

    boolean existsByUser_IdAndRole(Long userId, Role role);
}
