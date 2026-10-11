package com.starshop.service.vendor;

import com.starshop.domain.Promotion;
import com.starshop.domain.Store;
import com.starshop.domain.Voucher;
import com.starshop.domain.enums.CommonEnums.PromotionType;
import com.starshop.repository.PromotionRepository;
import com.starshop.repository.VendorShipperQueryRepository;
import com.starshop.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;

/**
 * Triển khai nghiệp vụ quản lý Khuyến mãi / Mã giảm giá của cửa hàng (F4.5).
 * @owner: TV2 | @leaf: F4.5 Khuyến mãi | @status: DONE
 */
@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private final VoucherRepository voucherRepository;
    private final PromotionRepository promotionRepository;
    private final VendorShipperQueryRepository queryRepository;
    private final StoreService storeService;

    @Override
    @Transactional(readOnly = true)
    public List<Voucher> myVouchers() {
        Store myStore = requireMyStore();
        return queryRepository.findVouchersByStoreId(myStore.getId());
    }

    @Override
    @Transactional
    public void createVoucher(String code, PromotionType type, BigDecimal value,
                              BigDecimal minOrder, BigDecimal maxDiscount, Integer quota) {
        Store myStore = requireMyStore();

        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Mã voucher không được để trống");
        }
        String normalizedCode = code.trim().toUpperCase(Locale.ROOT);
        if (type == null) {
            throw new IllegalArgumentException("Loại khuyến mãi không hợp lệ");
        }
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Giá trị giảm phải lớn hơn 0");
        }
        if (type == PromotionType.PRODUCT_PERCENT && value.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Phần trăm giảm giá sản phẩm không được vượt quá 100%");
        }
        if (quota != null && quota <= 0) {
            throw new IllegalArgumentException("Giới hạn lượt dùng phải lớn hơn 0");
        }

        Instant now = Instant.now();
        Instant end = now.plus(30, ChronoUnit.DAYS);

        Voucher voucher = Voucher.builder()
                .code(normalizedCode)
                .type(type)
                .value(value)
                .minOrder(minOrder != null ? minOrder : BigDecimal.ZERO)
                .maxDiscount(maxDiscount)
                .quota(quota != null ? quota : 100)
                .usedCount(0)
                .startAt(now)
                .endAt(end)
                .store(myStore)
                .active(true)
                .build();
        voucherRepository.save(voucher);

        // Lưu đồng bộ bản ghi chương trình khuyến mãi của shop
        Promotion promotion = Promotion.builder()
                .store(myStore)
                .type(type)
                .value(value)
                .startAt(now)
                .endAt(end)
                .quota(quota != null ? quota : 100)
                .build();
        promotionRepository.save(promotion);
    }

    @Override
    @Transactional
    public void toggleActive(Long voucherId, boolean active) {
        Store myStore = requireMyStore();
        Voucher voucher = queryRepository.findVoucherWithStoreById(voucherId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy mã giảm giá"));

        // Kiểm tra ownership (chống IDOR)
        if (voucher.getStore() == null || !voucher.getStore().getId().equals(myStore.getId())) {
            throw new IllegalArgumentException("Bạn không có quyền thay đổi mã giảm giá của cửa hàng khác");
        }

        voucher.setActive(active);
        voucherRepository.save(voucher);
    }

    private Store requireMyStore() {
        Store store = storeService.myStore();
        if (store == null) {
            throw new IllegalStateException("Bạn chưa đăng ký cửa hàng");
        }
        return store;
    }
}
