package com.starshop.service.customer;

import com.starshop.domain.*;
import com.starshop.domain.enums.OrderStatus;
import com.starshop.domain.enums.PaymentMethod;
import com.starshop.repository.*;
import com.starshop.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/** @owner: L. Đặt hàng COD/VNPAY, tách đơn theo shop. */
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final BigDecimal FLAT_SHIP = BigDecimal.valueOf(30000);

    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final CartItemRepository cartItemRepository;
    private final VoucherService voucherService;
    private final UserAddressRepository addressRepository;
    private final CurrentUser currentUser;

    @Override
    @Transactional
    public List<Order> placeFromCart(Long addressId, PaymentMethod method, String voucherCode) {
        Cart cart = cartService.getMyCart();
        if (cart.getItems().isEmpty()) throw new IllegalStateException("Giỏ hàng trống");

        String address = resolveAddress(addressId);

        // Nhóm item theo shop -> mỗi shop 1 đơn
        Map<Store, List<CartItem>> byStore = cart.getItems().stream()
                .collect(Collectors.groupingBy(i -> i.getProduct().getStore()));

        List<Order> created = new ArrayList<>();
        boolean voucherUsed = false;

        for (var entry : byStore.entrySet()) {
            Store store = entry.getKey();
            List<CartItem> items = entry.getValue();

            BigDecimal subtotal = items.stream()
                    .map(i -> i.getProduct().getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal discount = BigDecimal.ZERO;
            if (!voucherUsed) { // áp voucher cho đơn đầu tiên
                discount = voucherService.computeDiscount(voucherCode, subtotal);
                if (discount.signum() > 0) voucherUsed = true;
            }
            BigDecimal total = subtotal.add(FLAT_SHIP).subtract(discount).max(BigDecimal.ZERO);

            Order order = Order.builder()
                    .code("SS" + System.currentTimeMillis() + "-" + store.getId())
                    .buyer(currentUser.get())
                    .store(store)
                    .status(OrderStatus.NEW)
                    .paymentMethod(method)
                    .paid(false)
                    .subtotal(subtotal)
                    .shippingFee(FLAT_SHIP)
                    .discount(discount)
                    .total(total)
                    .shippingAddress(address)
                    .build();

            for (CartItem ci : items) {
                order.getItems().add(OrderItem.builder()
                        .order(order).product(ci.getProduct())
                        .quantity(ci.getQuantity()).unitPrice(ci.getProduct().getPrice()).build());
            }
            created.add(orderRepository.save(order));
        }

        cartService.clear();
        return created;
    }

    @Override
    public Page<Order> myOrders(OrderStatus status, Pageable pageable) {
        Long uid = currentUser.id();
        return (status == null)
                ? orderRepository.findByBuyerId(uid, pageable)
                : orderRepository.findByBuyerIdAndStatus(uid, status, pageable);
    }

    @Override
    public Order getMyOrder(Long orderId) {
        Order o = orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Không có đơn"));
        if (!o.getBuyer().getId().equals(currentUser.id()))
            throw new org.springframework.security.access.AccessDeniedException("Không phải đơn của bạn");
        return o;
    }

    @Override
    @Transactional
    public void markPaid(List<Long> orderIds) {
        Long uid = currentUser.id();
        for (Long id : orderIds) {
            orderRepository.findById(id)
                    .filter(o -> o.getBuyer().getId().equals(uid))
                    .ifPresent(o -> { o.setPaid(true); orderRepository.save(o); });
        }
    }

    private String resolveAddress(Long addressId) {
        if (addressId != null) {
            UserAddress a = addressRepository.findById(addressId).orElse(null);
            if (a != null && a.getUser().getId().equals(currentUser.id()))
                return a.getReceiverName() + " | " + a.getPhone() + " | " + a.getAddressLine()
                        + ", " + a.getWard() + ", " + a.getDistrict() + ", " + a.getProvince();
        }
        return currentUser.get().getFullName();
    }
}
