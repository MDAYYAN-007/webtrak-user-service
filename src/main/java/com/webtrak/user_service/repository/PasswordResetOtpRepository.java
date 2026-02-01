package com.webtrak.user_service.repository;

import com.webtrak.user_service.entity.PasswordResetOtp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PasswordResetOtpRepository
        extends JpaRepository<PasswordResetOtp, Long> {

    // Latest OTP (for cooldown check)
    Optional<PasswordResetOtp> findTopByUser_IdOrderByCreatedAtDesc(Long userId);

    // Latest UNUSED OTP
    Optional<PasswordResetOtp>
    findTopByUser_IdAndUsedFalseOrderByCreatedAtDesc(Long userId);
}
