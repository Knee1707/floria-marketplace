package com.starshop.service.admin;

import com.starshop.domain.Store;
import com.starshop.domain.User;
import com.starshop.domain.enums.Role;
import com.starshop.repository.AdminQueryRepository;
import com.starshop.repository.StoreRepository;
import com.starshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

/**
 * F6.1 Quản lý user (tìm kiếm, khoá/mở, đổi role) + F6.4 duyệt/cấm shop.
 * @owner: TV3 | @leaf: F6.1, F6.4 | @status: DONE
 */
@Service
@RequiredArgsConstructor
public class UserAdminServiceImpl implements UserAdminService {

    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final AdminQueryRepository adminQueryRepository;
    private final AdminScope adminScope;

    @Override
    @Transactional(readOnly = true)
    public Page<User> search(String keyword, Pageable pageable) {
        adminScope.requireAdmin();
        return adminQueryRepository.searchUsers(likePattern(keyword), pageable);
    }

    @Override
    @Transactional
    public void setLocked(Long userId, boolean locked) {
        adminScope.requireAdmin();
        if (userId.equals(adminScope.currentUserId())) {
            throw new IllegalArgumentException("Không thể tự khoá tài khoản của chính mình");
        }
        User u = findUser(userId);
        u.setLocked(locked);
        userRepository.save(u);
    }

    @Override
    @Transactional
    public void changeRole(Long userId, Role role) {
        adminScope.requireAdmin();
        if (role == null || role == Role.GUEST) {
            throw new IllegalArgumentException("Vai trò không hợp lệ");
        }
        if (userId.equals(adminScope.currentUserId())) {
            throw new IllegalArgumentException("Không thể tự đổi vai trò của chính mình");
        }
        User u = findUser(userId);
        u.setRole(role);
        userRepository.save(u);
    }

    @Override
    @Transactional
    public void setStoreApproved(Long storeId, boolean approved) {
        adminScope.requireStore(storeId); // Manager chỉ duyệt/cấm shop được giao
        Store s = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy cửa hàng #" + storeId));
        s.setApproved(approved);
        storeRepository.save(s);
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng #" + id));
    }

    /** "  Hoa " -> "%hoa%"; rỗng -> "%" (khớp tất cả). Dùng chung trong gói admin. */
    static String likePattern(String keyword) {
        if (keyword == null || keyword.isBlank()) return "%";
        String k = keyword.trim().toLowerCase(Locale.ROOT)
                .replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
        return "%" + k + "%";
    }
}
