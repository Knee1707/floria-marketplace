package com.starshop.security;

import com.starshop.domain.User;
import com.starshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/** Lấy user đang đăng nhập từ SecurityContext. Dùng chung mọi service. @owner: L. */
@Component
@RequiredArgsConstructor
public class CurrentUser {
    private final UserRepository userRepository;

    public User get() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) throw new IllegalStateException("Chưa đăng nhập");
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy user hiện tại"));
    }

    public Long id() {
        return get().getId();
    }
}
