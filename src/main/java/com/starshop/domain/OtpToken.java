package com.starshop.domain;

import com.starshop.domain.enums.CommonEnums.OtpPurpose;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/** OTP kích hoạt tài khoản / đặt lại mật khẩu. @owner: TV1 */
@Entity
@Table(name = "otp_tokens")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OtpToken extends BaseEntity {

    @Column(nullable = false, length = 150)
    private String email;

    @Column(nullable = false, length = 10)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OtpPurpose purpose;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private boolean used;
}
