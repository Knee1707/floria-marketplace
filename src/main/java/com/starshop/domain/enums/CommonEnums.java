package com.starshop.domain.enums;

/**
 * Gom các enum nhỏ dùng chung để đỡ nhiều file.
 * (Có thể tách thành file riêng nếu muốn.)
 */
public final class CommonEnums {
    private CommonEnums() {}

    /** Trạng thái giao của shipper. */
    public enum DeliveryStatus { ASSIGNED, DELIVERING, DELIVERED, FAILED }

    /** Loại khuyến mãi. */
    public enum PromotionType { PRODUCT_PERCENT, SHIPPING }

    /** Loại giao dịch ví. */
    public enum TransactionType { SALE, WITHDRAW, COMMISSION, REFUND }

    /** Mục đích OTP. */
    public enum OtpPurpose { REGISTER, RESET_PASSWORD }

    /** Trạng thái sản phẩm. */
    public enum ProductStatus { ACTIVE, HIDDEN, BANNED }

    /** Loại thông báo realtime. */
    public enum NotificationType { NEW_ORDER, ORDER_STATUS, ASSIGNMENT, CHAT, SYSTEM }

    /** Đối tượng được đánh giá sau khi đơn giao thành công: cửa hàng hoặc shipper. */
    public enum ReviewTarget { SHOP, SHIPPER }
}
