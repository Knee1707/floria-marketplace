package com.starshop.service.vendor;

import com.starshop.domain.Order;
import com.starshop.domain.enums.OrderStatus;

import java.util.List;

/**
 * HỢP ĐỒNG — TV2 viết VendorOrderServiceImpl (@owner: TV2 | F4.4).
 * Luồng 7 trạng thái + phân shipper (PHAN_CONG §8.1). Bắn NotificationService khi đổi trạng thái.
 */
public interface VendorOrderService {
    List<Order> shopOrders(OrderStatus status);           // null = tất cả
    void updateStatus(Long orderId, OrderStatus newStatus);
    void assignShipper(Long orderId, Long shipperId);     // tạo DeliveryAssignment + set order.shipper
}
