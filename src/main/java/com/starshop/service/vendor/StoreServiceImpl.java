package com.starshop.service.vendor;

import com.starshop.domain.Store;
import com.starshop.domain.User;
import com.starshop.domain.enums.CommonEnums.NotificationType;
import com.starshop.domain.enums.Role;
import com.starshop.repository.StoreRepository;
import com.starshop.repository.UserRepository;
import com.starshop.repository.VendorShipperQueryRepository;
import com.starshop.security.CurrentUser;
import com.starshop.service.common.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

/**
 * Triển khai nghiệp vụ Đăng ký shop (F4.1) và Quản lý thông tin shop (F4.2).
 * @owner: TV2 | @leaf: F4.1, F4.2 | @status: DONE
 */
@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {

    public static final String ACTIVE_STORE_SESSION_KEY = "ACTIVE_VENDOR_STORE_ID";

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final VendorShipperQueryRepository queryRepository;
    private final CurrentUser currentUser;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public Store registerStore(String name, String description) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Tên cửa hàng không được để trống");
        }

        User user = currentUser.get();
        List<Store> existingStores = queryRepository.findStoresByOwnerId(user.getId());
        if (!existingStores.isEmpty()) {
            return existingStores.get(0);
        }

        Store store = Store.builder()
                .name(name.trim())
                .description(description != null ? description.trim() : "")
                .owner(user)
                .approved(false)
                .build();
        Store saved = storeRepository.save(store);

        // Nâng cấp quyền user lên VENDOR và cập nhật ngay phiên đăng nhập hiện tại
        if (user.getRole() != Role.VENDOR) {
            user.setRole(Role.VENDOR);
            userRepository.save(user);
            refreshSessionRole(user);
        }

        notificationService.notify(
                user.getId(),
                NotificationType.SYSTEM,
                "Bạn đã đăng ký cửa hàng \"" + saved.getName() + "\" thành công. Cửa hàng đang chờ phê duyệt."
        );
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public Store myStore() {
        List<Store> stores = queryRepository.findStoresByOwnerId(currentUser.id());
        if (stores.isEmpty()) {
            return null;
        }
        Store selected = stores.get(0);
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attrs) {
            HttpServletRequest request = attrs.getRequest();
            request.setAttribute("myStores", stores);
            HttpSession session = request.getSession(false);
            if (session != null && session.getAttribute(ACTIVE_STORE_SESSION_KEY) instanceof Long activeId) {
                for (Store s : stores) {
                    if (s.getId().equals(activeId)) {
                        selected = s;
                        break;
                    }
                }
            }
        }
        return selected;
    }

    @Override
    @Transactional
    public void updateStore(Long storeId, String name, String description, String avatarUrl, String coverUrl) {
        if (storeId == null) {
            throw new IllegalArgumentException("Thiếu mã cửa hàng");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Tên cửa hàng không được để trống");
        }

        // Kiểm tra ownership (chống IDOR): chỉ chủ sở hữu mới được sửa cửa hàng của mình
        Store store = queryRepository.findStoreByIdAndOwnerId(storeId, currentUser.id())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy cửa hàng hoặc bạn không có quyền chỉnh sửa"));

        store.setName(name.trim());
        store.setDescription(description != null ? description.trim() : "");
        if (avatarUrl != null && !avatarUrl.isBlank()) {
            store.setAvatarUrl(avatarUrl.trim());
        }
        if (coverUrl != null && !coverUrl.isBlank()) {
            store.setCoverUrl(coverUrl.trim());
        }
        storeRepository.save(store);
    }

    private void refreshSessionRole(User user) {
        var currentAuth = SecurityContextHolder.getContext().getAuthentication();
        if (currentAuth != null) {
            var newAuth = new UsernamePasswordAuthenticationToken(
                    currentAuth.getPrincipal(),
                    currentAuth.getCredentials(),
                    List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
            );
            SecurityContextHolder.getContext().setAuthentication(newAuth);
        }
    }
}
