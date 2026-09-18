package com.starshop.service.auth;

import com.starshop.domain.User;
import com.starshop.domain.enums.CommonEnums.OtpPurpose;
import com.starshop.domain.enums.Role;
import com.starshop.dto.request.RegisterRequest;
import com.starshop.repository.UserRepository;
import com.starshop.service.common.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Nghiệp vụ tài khoản: đăng ký + kích hoạt OTP (F2.1, F2.2). @owner: TV1 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;
    private final MailService mailService;

    /** Tạo user (chưa kích hoạt) + gửi OTP về email. */
    @Transactional
    public void register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail()))
            throw new IllegalArgumentException("Email đã được đăng ký");
        if (userRepository.existsByPhone(req.getPhone()))
            throw new IllegalArgumentException("Số điện thoại đã được đăng ký");

        User user = User.builder()
                .email(req.getEmail())
                .phone(req.getPhone())
                .password(passwordEncoder.encode(req.getPassword())) // F2.5 BCrypt
                .fullName(req.getFullName())
                .role(Role.USER)
                .enabled(false)   // chờ OTP
                .locked(false)
                .build();
        userRepository.save(user);

        String code = otpService.createOtp(req.getEmail(), OtpPurpose.REGISTER);
        mailService.sendOtp(req.getEmail(), code);
    }

    /** Xác thực OTP -> kích hoạt tài khoản. */
    @Transactional
    public boolean activate(String email, String otp) {
        if (!otpService.verify(email, OtpPurpose.REGISTER, otp)) return false;
        userRepository.findByEmail(email).ifPresent(u -> { u.setEnabled(true); userRepository.save(u); });
        return true;
    }

    /** F2.4: gửi OTP đặt lại mật khẩu nếu email tồn tại (im lặng nếu không, tránh dò tài khoản). */
    @Transactional
    public void requestPasswordReset(String email) {
        userRepository.findByEmail(email).ifPresent(u -> {
            String code = otpService.createOtp(email, OtpPurpose.RESET_PASSWORD);
            mailService.sendOtp(email, code);
        });
    }

    /** F2.4: xác thực OTP + đặt mật khẩu mới (mã hoá BCrypt). */
    @Transactional
    public boolean resetPassword(String email, String otp, String newPassword) {
        if (!otpService.verify(email, OtpPurpose.RESET_PASSWORD, otp)) return false;
        return userRepository.findByEmail(email).map(u -> {
            u.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(u);
            return true;
        }).orElse(false);
    }
}
