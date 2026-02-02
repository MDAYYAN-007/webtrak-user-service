package com.webtrak.user_service.repository;

import com.webtrak.user_service.entity.UserRole;
import com.webtrak.user_service.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    List<UserRole> findByUser_Id(Long userId);

    @Query("""
        SELECT ur.user.id FROM UserRole ur
        WHERE ur.role = :role
    """)

    List<Long> findUserIdsByRole(@Param("role") Role role);

    void deleteByUser_Id(Long userId);

    boolean existsByUser_IdAndRole(Long userId, Role role);
}
