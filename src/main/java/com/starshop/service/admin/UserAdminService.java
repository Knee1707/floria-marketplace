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

    /** F6.1 (thêm): Admin tạo tài khoản mới (vd người bán / shipper), kích hoạt sẵn. */
    void createUser(String fullName, String email, String phone, String rawPassword, Role role);

    /** F6.1 (xóa): Admin xoá tài khoản. Chặn nếu còn dữ liệu liên quan (cửa hàng/đơn...). */
    void deleteUser(Long userId);
}
