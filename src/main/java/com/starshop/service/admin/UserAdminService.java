package com.starshop.service.admin;

import com.starshop.domain.User;
import com.starshop.domain.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * HỢP ĐỒNG — TV3 viết UserAdminServiceImpl (@owner: TV3 | F6.1, F6.4).
 * Cần thêm query tìm kiếm ở UserRepository → báo Leader.
 */
public interface UserAdminService {
    Page<User> search(String keyword, Pageable pageable);
    void setLocked(Long userId, boolean locked);
    void changeRole(Long userId, Role role);
    void setStoreApproved(Long storeId, boolean approved);
}
