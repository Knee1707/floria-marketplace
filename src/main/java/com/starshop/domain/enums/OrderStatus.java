package com.starshop.domain.enums;

/** Vòng đời đơn hàng (đề bài): mới -> xác nhận -> đã lấy -> đang giao -> đã giao -> huỷ / trả-hoàn. */
public enum OrderStatus {
    NEW, CONFIRMED, PICKED_UP, SHIPPING, DELIVERED, CANCELLED, RETURNED
}
