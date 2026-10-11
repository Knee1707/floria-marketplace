package com.starshop.service.admin;

import com.starshop.domain.Promotion;
import com.starshop.domain.Voucher;
import com.starshop.domain.enums.CommonEnums.PromotionType;
import com.starshop.dto.admin.AdminForms.PromotionForm;
import com.starshop.dto.admin.AdminForms.VoucherForm;
import com.starshop.repository.PromotionRepository;
import com.starshop.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/** @owner: TV3 | @leaf: F6.6 Khuyến mãi hệ thống | @status: DONE */
@Service
@RequiredArgsConstructor
public class SystemPromotionServiceImpl implements SystemPromotionService {

    private static final BigDecimal HUNDRED = new BigDecimal("100");

    private final VoucherRepository voucherRepository;
    private final PromotionRepository promotionRepository;
    private final AdminScope adminScope;

    @Override
    @Transactional(readOnly = true)
    public List<Voucher> vouchers() {
        return voucherRepository.findByStoreIsNull().stream()
                .sorted(Comparator.comparing(Voucher::getId).reversed()).toList();
    }

    @Override
    @Transactional
    public void createVoucher(VoucherForm f) {
        adminScope.requireAdmin();
        String code = f.code().trim().toUpperCase(Locale.ROOT);
        Example<Voucher> sameCode = Example.of(Voucher.builder().code(code).build(),
                ExampleMatcher.matchingAll().withIgnorePaths("active").withIgnoreCase("code"));
        if (voucherRepository.exists(sameCode)) {
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
                .store(null) // toàn hệ thống
                .active(true)
                .build());
    }

    @Override
    @Transactional
    public void setVoucherActive(Long voucherId, boolean active) {
        adminScope.requireAdmin();
        Voucher v = voucherRepository.findById(voucherId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy voucher #" + voucherId));
        if (v.getStore() != null) {
            throw new IllegalArgumentException("Voucher của cửa hàng do Vendor quản lý");
        }
        // Không xoá cứng: voucher có thể đã gắn vào đơn (order_vouchers) → chỉ bật/tắt.
        v.setActive(active);
        voucherRepository.save(v);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Promotion> promotions() {
        return promotionRepository.findByStoreIsNull().stream()
                .sorted(Comparator.comparing(Promotion::getId).reversed()).toList();
    }

    @Override
    @Transactional
    public void createPromotion(PromotionForm f) {
        adminScope.requireAdmin();
        validateValue(f.type(), f.value());
        Instant start = startOf(f.startDate());
        Instant end = endOf(f.endDate());
        validateRange(start, end);
        promotionRepository.save(Promotion.builder()
                .store(null).type(f.type()).value(f.value())
                .quota(f.quota()).startAt(start).endAt(end)
                .build());
    }

    @Override
    @Transactional
    public void deletePromotion(Long promotionId) {
        adminScope.requireAdmin();
        Promotion p = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khuyến mãi #" + promotionId));
        if (p.getStore() != null) {
            throw new IllegalArgumentException("Khuyến mãi của cửa hàng do Vendor quản lý");
        }
        promotionRepository.delete(p);
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
