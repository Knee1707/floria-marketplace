package com.starshop.domain;

import com.starshop.domain.enums.Role;
import jakarta.persistence.*;
import lombok.*;

/** Tài khoản người dùng (dùng chung cho cả 6 role). @owner: TV1 */
@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User extends BaseEntity {

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(length = 20, unique = true)
    private String phone;

    /** Mật khẩu đã mã hoá BCrypt. */
    @Column(nullable = false)
    private String password;

    @Column(length = 120)
    private String fullName;

    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    /** Đã kích hoạt qua OTP email chưa. */
    @Column(nullable = false)
    private boolean enabled;

    /** Bị Admin khoá hay không. */
    @Column(nullable = false)
    private boolean locked;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_level_id")
    private UserLevel userLevel;
}
