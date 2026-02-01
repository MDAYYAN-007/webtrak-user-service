package com.webtrak.user_service.service;

import com.webtrak.user_service.entity.PasswordResetOtp;
import com.webtrak.user_service.entity.User;
import com.webtrak.user_service.repository.PasswordResetOtpRepository;
import com.webtrak.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordService {

    private final UserRepository userRepository;
    private final PasswordResetOtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    private static final int OTP_EXPIRY_MINUTES = 10;
    private static final int OTP_COOLDOWN_MINUTES = 2;
    private static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d).{8,}$";


    public void forgotPassword(String email) {

        String normalizedEmail = email.trim().toLowerCase();

        User user = userRepository.findByEmail(normalizedEmail).orElse(null);

        // Security: do not reveal user existence
        if (user == null) {
            log.info("Password reset requested for non-existing email");
            return;
        }

        otpRepository.findTopByUser_IdOrderByCreatedAtDesc(user.getId())
                .ifPresent(lastOtp -> {
                    if (lastOtp.getCreatedAt()
                            .isAfter(LocalDateTime.now().minusMinutes(OTP_COOLDOWN_MINUTES))) {
                        throw new IllegalArgumentException(
                                "Please wait before requesting another OTP"
                        );
                    }
                });

        String otp = String.valueOf(
                100000 + new SecureRandom().nextInt(900000)
        );

        PasswordResetOtp resetOtp = new PasswordResetOtp(
                null,
                user,
                passwordEncoder.encode(otp),
                LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES),
                false,
                LocalDateTime.now()
        );

        otpRepository.save(resetOtp);

        log.info("Password reset OTP generated for userId={}", user.getId());
        emailService.sendOtpEmail(user.getEmail(), otp);
    }

    public void resetPassword(String email, String otp, String newPassword) {

        String normalizedEmail = email.trim().toLowerCase();

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new IllegalArgumentException("Invalid OTP or email"));

        PasswordResetOtp resetOtp = otpRepository
                .findTopByUser_IdAndUsedFalseOrderByCreatedAtDesc(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid OTP or email"));

        if (resetOtp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("OTP has expired");
        }

        if (!passwordEncoder.matches(otp, resetOtp.getOtpHash())) {
            throw new IllegalArgumentException("Invalid OTP or email");
        }

        validatePassword(newPassword);

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new IllegalArgumentException("New password cannot be same as old password");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetOtp.setUsed(true);
        otpRepository.save(resetOtp);

        log.info("Password reset successful for userId={}", user.getId());
    }

    public void changePassword(Long userId, String currentPassword, String newPassword) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new IllegalArgumentException("New password cannot be same as old password");
        }

        validatePassword(newPassword);

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        log.info("Password changed successfully for userId={}", userId);
    }

    private void validatePassword(String password) {
        if (!password.matches(PASSWORD_REGEX)) {
            throw new IllegalArgumentException(
                    "Password must be at least 8 characters and contain both letters and numbers"
            );
        }
    }
}
