package com.starshop.service.customer;

import java.math.BigDecimal;

/** Hợp đồng áp mã giảm giá (F3.9). @owner: L. */
public interface VoucherService {
    /** Trả số tiền được giảm cho subtotal; 0 nếu mã không hợp lệ. */
    BigDecimal computeDiscount(String code, BigDecimal subtotal);
}
