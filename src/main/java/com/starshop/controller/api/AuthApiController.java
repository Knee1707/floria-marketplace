package com.starshop.controller.api;

import com.starshop.repository.UserRepository;
import com.starshop.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST auth demo cho JWT (F2.7). POST /api/auth/login trả token; GET /api/me cần Bearer token.
 * @owner: L. Chứng minh JWT hoạt động (chức năng sáng tạo).
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthApiController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");
        return userRepository.findByEmail(email)
                .filter(u -> u.isEnabled() && !u.isLocked())
                .filter(u -> passwordEncoder.matches(password, u.getPassword()))
                .map(u -> ResponseEntity.ok(Map.of(
                        "token", jwtService.generate(u.getEmail(), u.getRole().name()),
                        "role", u.getRole().name(),
                        "fullName", u.getFullName() == null ? "" : u.getFullName())))
                .orElseGet(() -> ResponseEntity.status(401).body(Map.of("error", "Email hoặc mật khẩu không đúng")));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).body(Map.of("error", "Chưa xác thực"));
        return ResponseEntity.ok(Map.of("email", auth.getName(), "authorities", auth.getAuthorities()));
    }
}
