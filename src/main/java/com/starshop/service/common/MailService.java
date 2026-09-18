package com.starshop.service.common;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/** Gửi email OTP. @owner: TV1 (dùng chung cho đăng ký & quên mật khẩu). */
@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    public void sendOtp(String to, String code) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject("[StarShop] Mã xác thực OTP của bạn");
        msg.setText("Mã OTP của bạn là: " + code + "\nMã có hiệu lực trong 5 phút.");
        mailSender.send(msg);
    }
}
