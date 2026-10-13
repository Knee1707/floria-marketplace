package com.starshop.service.customer;

import com.starshop.domain.Order;
import com.starshop.domain.enums.OrderStatus;
import com.starshop.domain.enums.PaymentMethod;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/** Hợp đồng đặt hàng + lịch sử đơn (F3.10, F3.14). @owner: L. */
public interface OrderService {
    /** Đặt hàng từ giỏ hiện tại. Tách đơn theo shop. Trả danh sách đơn đã tạo. */
    List<Order> placeFromCart(Long addressId, PaymentMethod method, String voucherCode);

    /** Lịch sử đơn của tôi (lọc theo trạng thái nếu có). */
    Page<Order> myOrders(OrderStatus status, Pageable pageable);

    Order getMyOrder(Long orderId);

    /** Huỷ đơn của tôi (chỉ khi NEW), hoàn lại tồn kho. */
    void cancelMyOrder(Long orderId);

    /** Đánh dấu đã thanh toán (sau khi VNPAY trả về hợp lệ). Kiểm quyền sở hữu. */
    void markPaid(List<Long> orderIds);
}
