package com.starshop.service.auth;

import com.starshop.domain.OtpToken;
import com.starshop.domain.enums.CommonEnums.OtpPurpose;
import com.starshop.repository.OtpTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

/** Sinh & xác thực OTP (F2.2). @owner: TV1 */
@Service
@RequiredArgsConstructor
public class OtpService {

    private final OtpTokenRepository otpRepo;
    private final SecureRandom random = new SecureRandom();

    @Value("${starshop.otp.length:6}")
    private int length;
    @Value("${starshop.otp.ttl-minutes:5}")
    private int ttlMinutes;

    /** Tạo OTP mới cho email + mục đích, trả về mã (để MailService gửi). */
    public String createOtp(String email, OtpPurpose purpose) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) sb.append(random.nextInt(10));
        String code = sb.toString();

        OtpToken token = OtpToken.builder()
                .email(email)
                .code(code)
                .purpose(purpose)
                .expiresAt(Instant.now().plus(ttlMinutes, ChronoUnit.MINUTES))
                .used(false)
                .build();
        otpRepo.save(token);
        return code;
    }

    /** Kiểm tra OTP hợp lệ; nếu đúng thì đánh dấu đã dùng. */
    public boolean verify(String email, OtpPurpose purpose, String code) {
        return otpRepo.findTopByEmailAndPurposeAndUsedFalseOrderByCreatedAtDesc(email, purpose)
                .filter(t -> t.getCode().equals(code))
                .filter(t -> t.getExpiresAt().isAfter(Instant.now()))
                .map(t -> { t.setUsed(true); otpRepo.save(t); return true; })
                .orElse(false);
    }
}
