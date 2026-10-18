package com.starshop.service.admin;

import com.starshop.domain.User;
import com.starshop.domain.enums.Role;
import com.starshop.repository.StoreManagerRepository;
import com.starshop.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * PHẠM VI QUẢN TRỊ (F6.4) — 1 nguồn chân lý cho câu hỏi "người đang đăng nhập được quản lý shop nào?".
 * <ul>
 *   <li>ADMIN  → toàn hệ thống.</li>
 *   <li>MANAGER → chỉ các shop được phân công trong bảng store_managers.</li>
 * </ul>
 * Mọi service admin gọi lớp này để chống IDOR (không tin URL/tham số từ client).
 * @owner: TV3
 */
@Component
@RequiredArgsConstructor
public class AdminScope {

    private final CurrentUser currentUser;
    private final StoreManagerRepository storeManagerRepository;

    /** Phạm vi của người gọi. storeIds chỉ có nghĩa khi systemWide = false. */
    public record StoreScope(boolean systemWide, Set<Long> storeIds) {
        public boolean canAccess(Long storeId) {
            return systemWide || (storeId != null && storeIds.contains(storeId));
        }
        public boolean isEmpty() {
            return !systemWide && storeIds.isEmpty();
        }
    }

    @Transactional(readOnly = true)
    public StoreScope current() {
        User u = currentUser.get();
        if (u.getRole() == Role.ADMIN) {
            return new StoreScope(true, Set.of());
        }
        if (u.getRole() == Role.MANAGER) {
            Set<Long> ids = storeManagerRepository.findByManagerId(u.getId()).stream()
                    .map(sm -> sm.getStore().getId())
                    .collect(Collectors.toUnmodifiableSet());
            return new StoreScope(false, ids);
        }
        throw new AccessDeniedException("Chỉ Admin/Manager được truy cập khu quản trị");
    }

    /**
     * Cấu hình DÙNG CHUNG cả chuỗi mà đề cho cả Admin và Manager quản lý (danh mục, nhà vận chuyển):
     * chỉ cần là Admin hoặc Manager (current() tự chặn role khác).
     */
    public void requireStaff() {
        current();
    }

    /** Chặn thao tác cấu hình toàn hệ thống nếu không phải ADMIN (phòng thủ 2 lớp, ngoài SecurityConfig). */
    public void requireAdmin() {
        if (!current().systemWide()) {
            throw new AccessDeniedException("Chỉ Admin được thực hiện thao tác này");
        }
    }

    /** Chặn thao tác trên shop ngoài phạm vi (chống IDOR). */
    public void requireStore(Long storeId) {
        if (!current().canAccess(storeId)) {
            throw new AccessDeniedException("Cửa hàng này không thuộc phạm vi bạn quản lý");
        }
    }

    /** Id người đang đăng nhập (dùng để chặn tự khoá / tự hạ quyền). */
    public Long currentUserId() {
        return currentUser.id();
    }
}
