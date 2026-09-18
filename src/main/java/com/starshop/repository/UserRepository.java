package com.starshop.repository;

import com.starshop.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/** @owner: TV1 (Leader dựng, thành viên thêm query nếu cần qua Leader). */
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
}
