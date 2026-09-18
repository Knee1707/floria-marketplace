package com.starshop.service.shipper;

import com.starshop.domain.DeliveryAssignment;
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
}
