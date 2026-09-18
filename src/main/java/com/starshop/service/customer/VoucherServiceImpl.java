package com.starshop.service.customer;

import com.starshop.domain.Voucher;
import com.starshop.domain.enums.CommonEnums.PromotionType;
import com.starshop.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;

/** @owner: L. */
@Service
@RequiredArgsConstructor
public class VoucherServiceImpl implements VoucherService {

    private final VoucherRepository voucherRepository;

    @Override
    public BigDecimal computeDiscount(String code, BigDecimal subtotal) {
        if (code == null || code.isBlank()) return BigDecimal.ZERO;
        Voucher v = voucherRepository.findByCodeAndActiveTrue(code.trim()).orElse(null);
        if (v == null) return BigDecimal.ZERO;
        // hạn dùng
        Instant now = Instant.now();
        if (v.getStartAt() != null && now.isBefore(v.getStartAt())) return BigDecimal.ZERO;
        if (v.getEndAt() != null && now.isAfter(v.getEndAt())) return BigDecimal.ZERO;
        if (v.getMinOrder() != null && subtotal.compareTo(v.getMinOrder()) < 0) return BigDecimal.ZERO;
        if (v.getQuota() != null && v.getUsedCount() != null && v.getUsedCount() >= v.getQuota()) return BigDecimal.ZERO;

        BigDecimal discount;
        if (v.getType() == PromotionType.PRODUCT_PERCENT) {
            discount = subtotal.multiply(v.getValue()).divide(BigDecimal.valueOf(100));
        } else { // SHIPPING: giảm trực tiếp số tiền (phí ship)
            discount = v.getValue();
        }
        if (v.getMaxDiscount() != null && discount.compareTo(v.getMaxDiscount()) > 0) discount = v.getMaxDiscount();
        if (discount.compareTo(subtotal) > 0) discount = subtotal;
        return discount;
    }
}
