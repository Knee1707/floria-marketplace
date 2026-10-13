package com.starshop.service.shipper;

import com.starshop.domain.DeliveryAssignment;
import com.starshop.domain.Order;
import com.starshop.domain.User;
import com.starshop.domain.OrderItem;
import com.starshop.domain.Product;
import com.starshop.domain.Transaction;
import com.starshop.domain.enums.CommonEnums.DeliveryStatus;
import com.starshop.domain.enums.CommonEnums.NotificationType;
import com.starshop.domain.enums.CommonEnums.TransactionType;
import com.starshop.domain.enums.OrderStatus;
import com.starshop.repository.DeliveryAssignmentRepository;
import com.starshop.repository.OrderRepository;
import com.starshop.repository.ProductRepository;
import com.starshop.repository.TransactionRepository;
import com.starshop.repository.UserRepository;
import com.starshop.repository.VendorShipperQueryRepository;
import com.starshop.security.CurrentUser;
import com.starshop.service.common.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;

/**
 * Triển khai nghiệp vụ Giao vận cho Shipper (F5.1 Đơn được phân công, F5.2 Cập nhật giao, F5.3 Thống kê).
 * @owner: TV2 | @leaf: F5.1–F5.3 | @status: DONE
 */
@Service
@RequiredArgsConstructor
public class ShipperServiceImpl implements ShipperService {

    private static final ZoneId VN_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");

    private final DeliveryAssignmentRepository assignmentRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final TransactionRepository transactionRepository;
    private final VendorShipperQueryRepository queryRepository;
    private final UserRepository userRepository;
    private final CurrentUser currentUser;
    private final NotificationService notificationService;

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryAssignment> myAssignments() {
        return queryRepository.findAssignmentsByShipperId(currentUser.id());
    }

    @Override
    @Transactional
    public void updateDelivery(Long assignmentId, DeliveryStatus status) {
        if (assignmentId == null || status == null) {
            throw new IllegalArgumentException("Thông tin cập nhật giao hàng không hợp lệ");
        }
        Long shipperId = currentUser.id();

        // Kiểm tra ownership (chống IDOR): chỉ Shipper được phân công mới được cập nhật đơn này
        DeliveryAssignment assignment = queryRepository.findAssignmentWithDetails(assignmentId, shipperId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn phân công hoặc đơn không thuộc về bạn"));

        if (assignment.getStatus() == DeliveryStatus.DELIVERED || assignment.getStatus() == DeliveryStatus.FAILED) {
            throw new IllegalStateException("Đơn giao vận đã kết thúc, không thể thay đổi trạng thái");
        }

        Order order = assignment.getOrder();
        assignment.setStatus(status);

        if (status == DeliveryStatus.DELIVERING) {
            order.setStatus(OrderStatus.SHIPPING);
            orderRepository.save(order);
            assignmentRepository.save(assignment);

            notificationService.notify(
                    order.getBuyer().getId(),
                    NotificationType.ORDER_STATUS,
                    "Đơn hàng #" + order.getCode() + " đang trên đường giao đến bạn."
            );
        } else if (status == DeliveryStatus.DELIVERED) {
            assignment.setDeliveredAt(Instant.now());
            order.setStatus(OrderStatus.DELIVERED);
            order.setPaid(true);

            // Bám sát rules/database/05-metrics-media.md: cập nhật sold_count khi đơn DELIVERED
            for (OrderItem item : order.getItems()) {
                Product p = item.getProduct();
                if (p != null) {
                    long currentSold = p.getSoldCount() != null ? p.getSoldCount() : 0L;
                    p.setSoldCount(currentSold + item.getQuantity());
                    productRepository.save(p);
                }
            }

            orderRepository.save(order);
            assignmentRepository.save(assignment);

            // Ghi nhận giao dịch SALE & COMMISSION cho cửa hàng
            recordSaleTransactions(order);

            notificationService.notify(
                    order.getBuyer().getId(),
                    NotificationType.ORDER_STATUS,
                    "Đơn hàng #" + order.getCode() + " đã được giao thành công! Hãy đánh giá sản phẩm nhé."
            );
            if (order.getStore() != null && order.getStore().getOwner() != null) {
                notificationService.notify(
                        order.getStore().getOwner().getId(),
                        NotificationType.ORDER_STATUS,
                        "Shipper đã giao thành công đơn hàng #" + order.getCode()
                );
            }
        } else if (status == DeliveryStatus.FAILED) {
            order.setStatus(OrderStatus.RETURNED);
            // Hoàn lại tồn kho sản phẩm khi giao thất bại / hoàn trả
            for (OrderItem item : order.getItems()) {
                Product p = item.getProduct();
                if (p != null) {
                    p.setStock(p.getStock() + item.getQuantity());
                    productRepository.save(p);
                }
            }
            orderRepository.save(order);
            assignmentRepository.save(assignment);

            notificationService.notify(
                    order.getBuyer().getId(),
                    NotificationType.ORDER_STATUS,
                    "Đơn hàng #" + order.getCode() + " giao không thành công và đã chuyển sang trạng thái hoàn trả."
            );
            if (order.getStore() != null && order.getStore().getOwner() != null) {
                notificationService.notify(
                        order.getStore().getOwner().getId(),
                        NotificationType.ORDER_STATUS,
                        "Đơn hàng #" + order.getCode() + " giao không thành công (chuyển sang Trả/hoàn)."
                );
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> availableOrders() {
        return queryRepository.findClaimableOrders();
    }

    @Override
    @Transactional
    public void claimOrder(Long orderId) {
        if (orderId == null) throw new IllegalArgumentException("Thiếu mã đơn");
        Order order = queryRepository.lockOrderById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng"));
        if (order.getStatus() != OrderStatus.CONFIRMED || order.getShipper() != null) {
            throw new IllegalStateException("Đơn này không còn khả dụng (đã có người nhận hoặc đổi trạng thái)");
        }
        User shipper = userRepository.findById(currentUser.id())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản shipper"));

        order.setShipper(shipper);
        order.setStatus(OrderStatus.PICKED_UP);
        orderRepository.save(order);

        assignmentRepository.save(DeliveryAssignment.builder()
                .order(order).shipper(shipper)
                .status(DeliveryStatus.ASSIGNED).assignedAt(Instant.now()).build());

        notificationService.notify(order.getBuyer().getId(), NotificationType.ORDER_STATUS,
                "Đơn hàng #" + order.getCode() + " đã được shipper " + shipper.getFullName() + " nhận.");
        if (order.getStore() != null && order.getStore().getOwner() != null) {
            notificationService.notify(order.getStore().getOwner().getId(), NotificationType.ORDER_STATUS,
                    "Shipper " + shipper.getFullName() + " đã nhận đơn #" + order.getCode());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long countDeliveredThisMonth() {
        Instant startOfMonth = YearMonth.now(VN_ZONE)
                .atDay(1)
                .atStartOfDay(VN_ZONE)
                .toInstant();
        return queryRepository.countAssignmentsByShipperAndStatusSince(
                currentUser.id(),
                DeliveryStatus.DELIVERED,
                startOfMonth
        );
    }

    private void recordSaleTransactions(Order order) {
        BigDecimal total = order.getTotal() != null ? order.getTotal() : BigDecimal.ZERO;
        Transaction saleTx = Transaction.builder()
                .store(order.getStore())
                .order(order)
                .type(TransactionType.SALE)
                .amount(total)
                .note("Doanh thu đơn hàng #" + order.getCode())
                .build();
        transactionRepository.save(saleTx);

        BigDecimal commAmount = total.multiply(BigDecimal.valueOf(10))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        Transaction commTx = Transaction.builder()
                .store(order.getStore())
                .order(order)
                .type(TransactionType.COMMISSION)
                .amount(commAmount)
                .note("Chiết khấu sàn (10%) cho đơn #" + order.getCode())
                .build();
        transactionRepository.save(commTx);
    }
}
