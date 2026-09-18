package com.starshop.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

/** DTO đăng ký (F2.1) — validation ở đây. @owner: TV1 */
@Data
public class RegisterRequest {

    @NotBlank @Email
    private String email;

    @NotBlank
    @Pattern(regexp = "^0\\d{9}$", message = "Số điện thoại không hợp lệ")
    private String phone;

    @NotBlank @Size(min = 6, message = "Mật khẩu tối thiểu 6 ký tự")
    private String password;

    @NotBlank
    private String fullName;
}
