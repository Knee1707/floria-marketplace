package com.starshop.service.vendor;

import com.starshop.domain.Voucher;
import com.starshop.domain.enums.CommonEnums.PromotionType;

import java.math.BigDecimal;
import java.util.List;

/**
 * HỢP ĐỒNG — TV2 viết PromotionServiceImpl (@owner: TV2 | F4.5).
 * Voucher/Promotion gắn store_id = shop của vendor hiện tại.
 */
public interface PromotionService {
    List<Voucher> myVouchers();
    void createVoucher(String code, PromotionType type, BigDecimal value,
                       BigDecimal minOrder, BigDecimal maxDiscount, Integer quota);
    void toggleActive(Long voucherId, boolean active);
}
