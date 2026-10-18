package com.starshop.service.admin;

import com.starshop.domain.User;
import com.starshop.domain.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * HỢP ĐỒNG — TV3 viết UserAdminServiceImpl (@owner: TV3 | F6.1, F6.4).
 * Phạm vi: Admin = toàn hệ thống; Manager = user thuộc shop được giao (chủ shop + khách đã mua),
 * Manager chỉ khoá/mở; tạo / xoá / đổi vai trò là quyền riêng của Admin.
 */
public interface UserAdminService {
    /** role = null → mọi vai trò được phép xem trong phạm vi. */
    Page<User> search(String keyword, Role role, Pageable pageable);
    /** Vai trò người gọi được xem / lọc (Manager: USER, VENDOR). */
    List<Role> visibleRoles();
    void setLocked(Long userId, boolean locked);
    void changeRole(Long userId, Role role);
    void setStoreApproved(Long storeId, boolean approved);
    /** F6.1 (thêm): Admin tạo tài khoản mới (vd người bán / shipper / manager), kích hoạt sẵn. */
    void createUser(String fullName, String email, String phone, String rawPassword, Role role);
    /** F6.1 (xóa): Admin xoá tài khoản (chặn nếu còn dữ liệu liên quan). */
    void deleteUser(Long userId);
}
