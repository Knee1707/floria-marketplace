package com.starshop.service.admin;

import com.starshop.domain.Promotion;
import com.starshop.domain.Voucher;
import com.starshop.dto.admin.AdminForms.PromotionForm;
import com.starshop.dto.admin.AdminForms.VoucherForm;

import java.util.List;

/**
 * F6.6 Khuyến mãi HỆ THỐNG (store_id = null, áp dụng toàn sàn): voucher mã giảm giá + chương trình khuyến mãi.
 * Loại: PRODUCT_PERCENT (giảm % tiền hàng) hoặc SHIPPING (giảm phí ship). Chỉ Admin.
 * @owner: TV3 | @leaf: F6.6
 */
public interface SystemPromotionService {
    List<Voucher> vouchers();
    void createVoucher(VoucherForm form);
    void setVoucherActive(Long voucherId, boolean active);

    List<Promotion> promotions();
    void createPromotion(PromotionForm form);
    void deletePromotion(Long promotionId);
}
