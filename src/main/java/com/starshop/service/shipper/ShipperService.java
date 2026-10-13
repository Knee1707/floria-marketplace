package com.starshop.service.shipper;

import com.starshop.domain.DeliveryAssignment;
import com.starshop.domain.Order;
import com.starshop.domain.enums.CommonEnums.DeliveryStatus;

import java.util.List;

/**
 * HỢP ĐỒNG — TV2 viết ShipperServiceImpl (@owner: TV2 | F5.1–F5.3).
 * Kiểm assignment thuộc shipper hiện tại. Đồng bộ Order.status khi giao xong.
 */
public interface ShipperService {
    List<DeliveryAssignment> myAssignments();
    void updateDelivery(Long assignmentId, DeliveryStatus status);
    long countDeliveredThisMonth();

    /** Đơn đã được shop xác nhận, chưa có shipper — shipper có thể tự nhận. */
    List<Order> availableOrders();

    /** Shipper tự nhận đơn: gán shipper, đơn -> PICKED_UP, tạo phân công ASSIGNED. */
    void claimOrder(Long orderId);
}
