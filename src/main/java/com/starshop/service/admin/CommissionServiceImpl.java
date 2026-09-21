package com.starshop.service.admin;

import com.starshop.domain.Commission;
import com.starshop.domain.Store;
import com.starshop.repository.AdminQueryRepository;
import com.starshop.repository.CommissionRepository;
import com.starshop.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * F6.5 Chiết khấu (hoa hồng) app thu của shop.
 * - Dòng có store = null là MỨC MẶC ĐỊNH toàn sàn (chỉ Admin sửa).
 * - Manager chỉ xem/sửa chiết khấu của shop được phân công.
 * @owner: TV3 | @leaf: F6.5 | @status: DONE
 */
@Service
@RequiredArgsConstructor
public class CommissionServiceImpl implements CommissionService {

    private static final BigDecimal MAX_RATE = new BigDecimal("100");

    private final CommissionRepository commissionRepository;
    private final StoreRepository storeRepository;
    private final AdminQueryRepository adminQueryRepository;
    private final AdminScope adminScope;

    /** Trả danh sách TRONG PHẠM VI người gọi (Admin: tất cả; Manager: shop được giao). */
    @Override
    @Transactional(readOnly = true)
    public List<Commission> all() {
        AdminScope.StoreScope scope = adminScope.current();
        if (scope.systemWide()) return adminQueryRepository.findAllCommissionsWithStore();
        if (scope.isEmpty()) return List.of();
        return adminQueryRepository.findCommissionsWithStoreIn(scope.storeIds());
    }

    @Override
    @Transactional
    public void save(Long id, Long storeId, BigDecimal rate, String note) {
        AdminScope.StoreScope scope = adminScope.current();
        if (rate == null || rate.signum() < 0 || rate.compareTo(MAX_RATE) > 0) {
            throw new IllegalArgumentException("Tỉ lệ chiết khấu phải từ 0 đến 100%");
        }
        if (storeId == null && !scope.systemWide()) {
            throw new AccessDeniedException("Chỉ Admin được đặt mức chiết khấu mặc định toàn sàn");
        }
        if (storeId != null && !scope.canAccess(storeId)) {
            throw new AccessDeniedException("Cửa hàng này không thuộc phạm vi bạn quản lý");
        }

        Commission c;
        if (id == null) {
            c = new Commission();
        } else {
            c = adminQueryRepository.findCommissionWithStore(id)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy chiết khấu #" + id));
            checkAccess(scope, c);
        }
        Store store = null;
        if (storeId != null) {
            store = storeRepository.findById(storeId)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy cửa hàng #" + storeId));
        }
        // Mỗi shop (và mức mặc định) chỉ có 1 dòng chiết khấu → tránh 2 nguồn chân lý.
        final Long selfId = c.getId();
        boolean duplicated = (storeId == null
                ? adminQueryRepository.findAllCommissionsWithStore().stream().filter(x -> x.getStore() == null)
                : commissionRepository.findByStoreId(storeId).stream())
                .anyMatch(x -> !x.getId().equals(selfId));
        if (duplicated) {
            throw new IllegalArgumentException(storeId == null
                    ? "Đã có mức mặc định toàn sàn — hãy sửa dòng đó"
                    : "Cửa hàng này đã có chiết khấu — hãy sửa dòng đó");
        }
        c.setStore(store);
        c.setRate(rate);
        c.setNote(note == null || note.isBlank() ? null : note.trim());
        commissionRepository.save(c);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Commission c = adminQueryRepository.findCommissionWithStore(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy chiết khấu #" + id));
        checkAccess(adminScope.current(), c);
        commissionRepository.delete(c);
    }

    private static void checkAccess(AdminScope.StoreScope scope, Commission c) {
        Long storeId = c.getStore() == null ? null : c.getStore().getId();
        boolean ok = scope.systemWide() || (storeId != null && scope.canAccess(storeId));
        if (!ok) throw new AccessDeniedException("Chiết khấu này không thuộc phạm vi bạn quản lý");
    }
}
