package com.starshop.service.admin;

import com.starshop.domain.Product;
import com.starshop.domain.Store;
import com.starshop.domain.StoreManager;
import com.starshop.domain.User;
import com.starshop.domain.enums.CommonEnums.ProductStatus;
import com.starshop.domain.enums.Role;
import com.starshop.repository.AdminQueryRepository;
import com.starshop.repository.ProductRepository;
import com.starshop.repository.StoreManagerRepository;
import com.starshop.repository.StoreRepository;
import com.starshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/** @owner: TV3 | @leaf: F6.4 Duyệt/cấm shop & SP + phân công Manager | @status: DONE */
@Service
@RequiredArgsConstructor
public class ModerationServiceImpl implements ModerationService {

    private final AdminQueryRepository adminQueryRepository;
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final StoreManagerRepository storeManagerRepository;
    private final UserRepository userRepository;
    private final AdminScope adminScope;

    @Override
    @Transactional(readOnly = true)
    public List<Store> stores() {
        AdminScope.StoreScope scope = adminScope.current();
        if (scope.systemWide()) return adminQueryRepository.findAllStoresWithOwner();
        if (scope.isEmpty()) return List.of();
        return adminQueryRepository.findStoresWithOwnerByIdIn(scope.storeIds());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> products(String keyword, Long storeId, Pageable pageable) {
        AdminScope.StoreScope scope = adminScope.current();
        String kw = UserAdminServiceImpl.likePattern(keyword);
        if (storeId != null) {
            adminScope.requireStore(storeId); // Manager không xem được SP shop ngoài phạm vi qua tham số URL
            return adminQueryRepository.searchProductsInStores(kw, Set.of(storeId), pageable);
        }
        if (scope.systemWide()) return adminQueryRepository.searchProducts(kw, pageable);
        if (scope.isEmpty()) return Page.empty(pageable);
        return adminQueryRepository.searchProductsInStores(kw, scope.storeIds(), pageable);
    }

    @Override
    @Transactional
    public void setProductStatus(Long productId, ProductStatus status) {
        if (status != ProductStatus.ACTIVE && status != ProductStatus.BANNED) {
            throw new IllegalArgumentException("Quản trị chỉ được cấm (BANNED) hoặc mở lại (ACTIVE) sản phẩm");
        }
        Product p = adminQueryRepository.findProductWithStore(productId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm #" + productId));
        adminScope.requireStore(p.getStore().getId()); // chống IDOR cho Manager
        p.setStatus(status);
        productRepository.save(p);
    }

    // ===================== Phân công Manager ↔ shop =====================

    @Override
    @Transactional(readOnly = true)
    public Map<Long, List<User>> managersByStore(List<Store> stores) {
        AdminScope.StoreScope scope = adminScope.current();
        Set<Long> ids = stores.stream().map(Store::getId).filter(scope::canAccess).collect(Collectors.toSet());
        if (ids.isEmpty()) return Map.of();
        return storeManagerRepository.findWithManagerByStoreIdIn(ids).stream()
                .collect(Collectors.groupingBy(sm -> sm.getStore().getId(),
                        Collectors.mapping(StoreManager::getManager, Collectors.toList())));
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> managerCandidates() {
        adminScope.requireAdmin();
        return adminQueryRepository.findUsersByRole(Role.MANAGER);
    }

    @Override
    @Transactional
    public void assignManager(Long storeId, Long managerId) {
        adminScope.requireAdmin();
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy cửa hàng #" + storeId));
        User manager = userRepository.findById(managerId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng #" + managerId));
        if (manager.getRole() != Role.MANAGER) {
            throw new IllegalArgumentException("Chỉ phân công được tài khoản có vai trò MANAGER");
        }
        if (storeManagerRepository.existsByStoreIdAndManagerId(storeId, managerId)) {
            throw new IllegalArgumentException("Manager này đã phụ trách cửa hàng " + store.getName());
        }
        storeManagerRepository.save(StoreManager.builder().store(store).manager(manager).build());
    }

    @Override
    @Transactional
    public void unassignManager(Long storeId, Long managerId) {
        adminScope.requireAdmin();
        StoreManager sm = storeManagerRepository.findByStoreIdAndManagerId(storeId, managerId)
                .orElseThrow(() -> new IllegalArgumentException("Manager này không phụ trách cửa hàng #" + storeId));
        storeManagerRepository.delete(sm);
    }
}
