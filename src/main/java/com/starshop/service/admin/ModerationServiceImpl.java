package com.starshop.service.admin;

import com.starshop.domain.Product;
import com.starshop.domain.Store;
import com.starshop.domain.enums.CommonEnums.ProductStatus;
import com.starshop.repository.AdminQueryRepository;
import com.starshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** @owner: TV3 | @leaf: F6.4 Duyệt/cấm shop & SP | @status: DONE */
@Service
@RequiredArgsConstructor
public class ModerationServiceImpl implements ModerationService {

    private final AdminQueryRepository adminQueryRepository;
    private final ProductRepository productRepository;
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
    public Page<Product> products(String keyword, Pageable pageable) {
        AdminScope.StoreScope scope = adminScope.current();
        String kw = UserAdminServiceImpl.likePattern(keyword);
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
}
