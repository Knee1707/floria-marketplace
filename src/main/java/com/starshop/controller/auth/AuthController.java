package com.starshop.controller.auth;

import com.starshop.dto.request.RegisterRequest;
import com.starshop.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * Đăng nhập / Đăng ký / Kích hoạt OTP (D2). @owner: TV1.
 * Đăng nhập/đăng xuất do Spring Security xử lý (xem SecurityConfig).
 */
@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("form", new RegisterRequest());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("form") RegisterRequest form,
                           BindingResult binding, Model model) {
        if (binding.hasErrors()) return "auth/register";
        try {
            authService.register(form);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            return "auth/register";
        }
        model.addAttribute("email", form.getEmail());
        return "auth/verify-otp";
    }

    @PostMapping("/verify")
    public String verify(@RequestParam String email, @RequestParam String otp, Model model) {
        if (authService.activate(email, otp)) {
            return "redirect:/auth/login?activated";
        }
        model.addAttribute("email", email);
        model.addAttribute("error", "OTP không đúng hoặc đã hết hạn");
        return "auth/verify-otp";
    }

    // ===== F2.4 Quên mật khẩu =====
    @GetMapping("/forgot")
    public String forgotPage() {
        return "auth/forgot";
    }

    @PostMapping("/forgot")
    public String forgot(@RequestParam String email, Model model) {
        authService.requestPasswordReset(email);
        model.addAttribute("email", email);
        return "auth/reset"; // luôn sang bước nhập OTP (không tiết lộ email có tồn tại hay không)
    }

    @PostMapping("/reset")
    public String reset(@RequestParam String email, @RequestParam String otp,
                        @RequestParam String newPassword, Model model) {
        if (authService.resetPassword(email, otp, newPassword)) {
            return "redirect:/auth/login?reset";
        }
        model.addAttribute("email", email);
        model.addAttribute("error", "OTP không đúng hoặc đã hết hạn");
        return "auth/reset";
    }
}
