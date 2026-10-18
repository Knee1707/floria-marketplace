package com.starshop.service.vendor;

import com.starshop.domain.DeliveryAssignment;
import com.starshop.domain.Order;
import com.starshop.domain.OrderItem;
import com.starshop.domain.Product;
import com.starshop.domain.Store;
import com.starshop.domain.Transaction;
import com.starshop.domain.User;
import com.starshop.domain.enums.CommonEnums.DeliveryStatus;
import com.starshop.domain.enums.CommonEnums.NotificationType;
import com.starshop.domain.enums.CommonEnums.TransactionType;
import com.starshop.domain.enums.OrderStatus;
import com.starshop.domain.enums.Role;
import com.starshop.repository.DeliveryAssignmentRepository;
import com.starshop.repository.OrderRepository;
import com.starshop.repository.ProductRepository;
import com.starshop.repository.TransactionRepository;
import com.starshop.repository.UserRepository;
import com.starshop.repository.VendorShipperQueryRepository;
import com.starshop.service.common.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Triển khai nghiệp vụ quản lý đơn hàng của shop theo luồng 7 trạng thái và phân công Shipper (F4.4).
 * @owner: TV2 | @leaf: F4.4 Đơn shop (7 trạng thái) | @status: DONE
 */
@Service
@RequiredArgsConstructor
public class VendorOrderServiceImpl implements VendorOrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final DeliveryAssignmentRepository assignmentRepository;
    private final TransactionRepository transactionRepository;
    private final VendorShipperQueryRepository queryRepository;
    private final StoreService storeService;
    private final NotificationService notificationService;

    @Override
    @Transactional(readOnly = true)
    public List<Order> shopOrders(OrderStatus status) {
        Store myStore = requireMyStore();
        if (status == null) {
            return queryRepository.findOrdersByStoreId(myStore.getId());
        }
        return queryRepository.findOrdersByStoreIdAndStatus(myStore.getId(), status);
    }

    @Override
    @Transactional
    public void updateStatus(Long orderId, OrderStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("Trạng thái mới không hợp lệ");
        }
        Store myStore = requireMyStore();
        Order order = queryRepository.findOrderWithDetailsById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng"));

        // Kiểm tra ownership (chống IDOR)
        if (!order.getStore().getId().equals(myStore.getId())) {
            throw new IllegalArgumentException("Bạn không có quyền cập nhật đơn hàng của cửa hàng khác");
        }

        OrderStatus current = order.getStatus();
        if (current == OrderStatus.CANCELLED || current == OrderStatus.DELIVERED || current == OrderStatus.RETURNED) {
            throw new IllegalStateException("Đơn hàng đã ở trạng thái kết thúc (" + statusLabel(current) + "), không thể đổi trạng thái");
        }

        // Kiểm tra luồng chuyển trạng thái hợp lệ phía Cửa hàng (NEW -> CONFIRMED/CANCELLED; CONFIRMED -> PICKED_UP/CANCELLED)
        boolean validTransition = (current == OrderStatus.NEW
                && (newStatus == OrderStatus.CONFIRMED || newStatus == OrderStatus.CANCELLED))
                || (current == OrderStatus.CONFIRMED
                && (newStatus == OrderStatus.PICKED_UP || newStatus == OrderStatus.CANCELLED));

        if (!validTransition) {
            throw new IllegalStateException("Không thể chuyển trạng thái đơn hàng từ \""
                    + statusLabel(current) + "\" sang \"" + statusLabel(newStatus) + "\"");
        }

        // Nếu huỷ đơn thì hoàn lại tồn kho và ghi nhận hoàn tiền (nếu đơn đã thanh toán trước)
        if (newStatus == OrderStatus.CANCELLED) {
            for (OrderItem item : order.getItems()) {
                Product p = item.getProduct();
                if (p != null) {
                    p.setStock(p.getStock() + item.getQuantity());
                    productRepository.save(p);
                }
            }
            if (order.isPaid()) {
                BigDecimal refundAmount = order.getTotal() != null ? order.getTotal() : BigDecimal.ZERO;
                Transaction refundTx = Transaction.builder()
                        .store(myStore)
                        .order(order)
                        .type(TransactionType.REFUND)
                        .amount(refundAmount)
                        .note("Hoàn tiền do huỷ đơn hàng #" + order.getCode())
                        .build();
                transactionRepository.save(refundTx);
                order.setPaid(false);
            }
        }

        order.setStatus(newStatus);
        orderRepository.save(order);

        notificationService.notify(
                order.getBuyer().getId(),
                NotificationType.ORDER_STATUS,
                "Đơn hàng #" + order.getCode() + " tại " + myStore.getName()
                        + " đã được cập nhật sang trạng thái: " + statusLabel(newStatus)
        );
    }

    @Override
    @Transactional
    public void assignShipper(Long orderId, Long shipperId) {
        Store myStore = requireMyStore();
        Order order = queryRepository.findOrderWithDetailsById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng"));

        if (!order.getStore().getId().equals(myStore.getId())) {
            throw new IllegalArgumentException("Bạn không có quyền phân công Shipper cho đơn của cửa hàng khác");
        }
        if (order.getStatus() == OrderStatus.CANCELLED
                || order.getStatus() == OrderStatus.DELIVERED
                || order.getStatus() == OrderStatus.RETURNED) {
            throw new IllegalStateException("Không thể phân công Shipper cho đơn đã kết thúc");
        }
        if (order.getStatus() == OrderStatus.SHIPPING) {
            throw new IllegalStateException("Đơn hàng đang trên đường giao, không thể phân công lại Shipper");
        }

        User shipper = userRepository.findById(shipperId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản Shipper"));
        if (shipper.getRole() != Role.SHIPPER || !shipper.isEnabled() || shipper.isLocked()) {
            throw new IllegalArgumentException("Tài khoản được chọn không phải là Shipper đang hoạt động");
        }

        order.setShipper(shipper);
        if (order.getStatus() == OrderStatus.NEW || order.getStatus() == OrderStatus.CONFIRMED) {
            order.setStatus(OrderStatus.PICKED_UP);
        }
        orderRepository.save(order);

        // Kiểm tra nếu đã có bản ghi phân công cho đơn này thì cập nhật, chưa có thì tạo mới
        List<DeliveryAssignment> existing = queryRepository.findAssignmentsByOrderId(order.getId());
        DeliveryAssignment assignment;
        if (!existing.isEmpty()) {
            assignment = existing.get(0);
            assignment.setShipper(shipper);
            assignment.setStatus(DeliveryStatus.ASSIGNED);
            assignment.setAssignedAt(Instant.now());
        } else {
            assignment = DeliveryAssignment.builder()
                    .order(order)
                    .shipper(shipper)
                    .status(DeliveryStatus.ASSIGNED)
                    .assignedAt(Instant.now())
                    .build();
        }
        assignmentRepository.save(assignment);

        // Bắn thông báo realtime cho cả Shipper và người mua
        notificationService.notify(
                shipper.getId(),
                NotificationType.ASSIGNMENT,
                "Bạn vừa được cửa hàng \"" + myStore.getName() + "\" phân công giao đơn #" + order.getCode()
        );
        notificationService.notify(
                order.getBuyer().getId(),
                NotificationType.ORDER_STATUS,
                "Đơn hàng #" + order.getCode() + " đã được bàn giao cho Shipper " + shipper.getFullName()
        );
    }

    private Store requireMyStore() {
        Store store = storeService.myStore();
        if (store == null) {
            throw new IllegalStateException("Bạn chưa đăng ký cửa hàng");
        }
        return store;
    }

    private String statusLabel(OrderStatus s) {
        return switch (s) {
            case NEW -> "Đơn mới";
            case CONFIRMED -> "Đã xác nhận";
            case PICKED_UP -> "Đã lấy hàng";
            case SHIPPING -> "Đang giao";
            case DELIVERED -> "Đã giao";
            case CANCELLED -> "Đã huỷ";
            case RETURNED -> "Trả/hoàn";
        };
    }
}
