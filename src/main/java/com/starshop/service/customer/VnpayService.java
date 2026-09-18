package com.starshop.service.customer;

import java.math.BigDecimal;
import java.util.Map;

/** Thanh toán VNPAY sandbox (F3.11 — chức năng sáng tạo). @owner: L. */
public interface VnpayService {
    /** Tạo URL chuyển hướng sang cổng VNPAY. */
    String createPaymentUrl(BigDecimal amountVnd, String txnRef, String orderInfo, String clientIp);
    /** Xác thực chữ ký của tham số trả về từ VNPAY. */
    boolean verifyReturn(Map<String, String> params);
}
