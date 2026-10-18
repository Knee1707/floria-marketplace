package com.starshop.service.admin;

import com.starshop.domain.Promotion;
import com.starshop.domain.Store;
import com.starshop.domain.Voucher;
import com.starshop.domain.enums.CommonEnums.PromotionType;
import com.starshop.dto.admin.AdminForms.PromotionForm;
import com.starshop.dto.admin.AdminForms.VoucherForm;
import com.starshop.repository.AdminQueryRepository;
import com.starshop.repository.PromotionRepository;
import com.starshop.repository.StoreRepository;
import com.starshop.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

/**
 * F6.6 Khuyến mãi: voucher + chương trình (giảm % sản phẩm / giảm phí vận chuyển).
 * - store = null : TOÀN SÀN → chỉ Admin tạo / sửa.
 * - store != null: của 1 shop → Admin (mọi shop) hoặc Manager (shop được phân công).
 * @owner: TV3 | @leaf: F6.6 | @status: DONE
 */
@Service
@RequiredArgsConstructor
public class SystemPromotionServiceImpl implements SystemPromotionService {

    private static final BigDecimal HUNDRED = new BigDecimal("100");

    private final VoucherRepository voucherRepository;
    private final PromotionRepository promotionRepository;
    private final StoreRepository storeRepository;
    private final AdminQueryRepository adminQueryRepository;
    private final AdminScope adminScope;

    @Override
    @Transactional(readOnly = true)
    public List<Voucher> vouchers() {
        AdminScope.StoreScope scope = adminScope.current();
        if (scope.systemWide()) return adminQueryRepository.findAllVouchersWithStore();
        if (scope.isEmpty()) return List.of();
        return adminQueryRepository.findVouchersWithStoreIn(scope.storeIds());
    }

    @Override
    @Transactional
    public void createVoucher(VoucherForm f) {
        Store store = targetStore(f.storeId());
        String code = f.code().trim().toUpperCase(Locale.ROOT);
        if (adminQueryRepository.existsVoucherCode(code)) {
            throw new IllegalArgumentException("Mã " + code + " đã tồn tại");
        }
        validateValue(f.type(), f.value());
        Instant start = startOf(f.startDate());
        Instant end = endOf(f.endDate());
        validateRange(start, end);
        voucherRepository.save(Voucher.builder()
                .code(code).type(f.type()).value(f.value())
                .minOrder(f.minOrder()).maxDiscount(f.maxDiscount())
                .quota(f.quota()).usedCount(0)
                .startAt(start).endAt(end)
                .store(store)
                .active(true)
                .build());
    }

    @Override
    @Transactional
    public void setVoucherActive(Long voucherId, boolean active) {
        Voucher v = adminQueryRepository.findVoucherWithStore(voucherId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy voucher #" + voucherId));
        requireAccess(v.getStore());
        // Không xoá cứng: voucher có thể đã gắn vào đơn (order_vouchers) → chỉ bật/tắt.
        v.setActive(active);
        voucherRepository.save(v);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Promotion> promotions() {
        AdminScope.StoreScope scope = adminScope.current();
        if (scope.systemWide()) return adminQueryRepository.findAllPromotionsWithStore();
        if (scope.isEmpty()) return List.of();
        return adminQueryRepository.findPromotionsWithStoreIn(scope.storeIds());
    }

    @Override
    @Transactional
    public void createPromotion(PromotionForm f) {
        Store store = targetStore(f.storeId());
        validateValue(f.type(), f.value());
        Instant start = startOf(f.startDate());
        Instant end = endOf(f.endDate());
        validateRange(start, end);
        promotionRepository.save(Promotion.builder()
                .store(store).type(f.type()).value(f.value())
                .quota(f.quota()).startAt(start).endAt(end)
                .build());
    }

    @Override
    @Transactional
    public void deletePromotion(Long promotionId) {
        Promotion p = adminQueryRepository.findPromotionWithStore(promotionId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khuyến mãi #" + promotionId));
        requireAccess(p.getStore());
        promotionRepository.delete(p);
    }

    /** Shop đích của khuyến mãi mới: null = toàn sàn (chỉ Admin); ngược lại phải trong phạm vi người gọi. */
    private Store targetStore(Long storeId) {
        if (storeId == null) {
            adminScope.requireAdmin();
            return null;
        }
        adminScope.requireStore(storeId);
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy cửa hàng #" + storeId));
    }

    /** Sửa / xoá khuyến mãi có sẵn: toàn sàn → Admin; của shop → phải trong phạm vi (chống IDOR). */
    private void requireAccess(Store store) {
        AdminScope.StoreScope scope = adminScope.current();
        boolean ok = (store == null) ? scope.systemWide() : scope.canAccess(store.getId());
        if (!ok) throw new AccessDeniedException("Khuyến mãi này không thuộc phạm vi bạn quản lý");
    }

    private static void validateValue(PromotionType type, BigDecimal value) {
        if (type == PromotionType.PRODUCT_PERCENT && value.compareTo(HUNDRED) > 0) {
            throw new IllegalArgumentException("Giảm theo % không được vượt quá 100");
        }
    }

    private static void validateRange(Instant start, Instant end) {
        if (start != null && end != null && end.isBefore(start)) {
            throw new IllegalArgumentException("Ngày kết thúc phải sau ngày bắt đầu");
        }
    }

    private static Instant startOf(LocalDate d) {
        return d == null ? null : d.atStartOfDay(StatsServiceImpl.VN).toInstant();
    }

    /** Hết ngày kết thúc (23:59:59.999 giờ VN). */
    private static Instant endOf(LocalDate d) {
        return d == null ? null : d.plusDays(1).atStartOfDay(StatsServiceImpl.VN).toInstant().minusMillis(1);
    }
}
