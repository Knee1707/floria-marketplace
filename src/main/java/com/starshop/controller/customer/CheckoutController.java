package com.starshop.controller.customer;

import com.starshop.domain.Order;
import com.starshop.domain.enums.PaymentMethod;
import com.starshop.repository.UserAddressRepository;
import com.starshop.security.CurrentUser;
import com.starshop.service.customer.CartService;
import com.starshop.service.customer.OrderService;
import com.starshop.service.customer.VnpayService;
import com.starshop.service.customer.VoucherService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/** Thanh toán COD/VNPAY + voucher (F3.9–F3.11). @owner: L. */
@Controller
@RequestMapping("/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private static final String SESSION_ORDER_IDS = "vnpOrderIds";

    private final CartService cartService;
    private final OrderService orderService;
    private final VoucherService voucherService;
    private final VnpayService vnpayService;
    private final UserAddressRepository addressRepository;
    private final CurrentUser currentUser;

    @GetMapping
    public String view(@RequestParam(required = false) String voucher,
                       @RequestParam(required = false) List<Long> itemIds, Model model) {
        var cart = cartService.getMyCart();
        boolean selectAll = (itemIds == null || itemIds.isEmpty());
        var items = cart.getItems().stream()
                .filter(i -> selectAll || itemIds.contains(i.getId()))
                .toList();
        BigDecimal subtotal = items.stream()
                .map(i -> i.getProduct().getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal discount = voucherService.computeDiscount(voucher, subtotal);
        model.addAttribute("cart", cart);
        model.addAttribute("items", items);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("discount", discount);
        model.addAttribute("voucher", voucher);
        model.addAttribute("itemIds", selectAll ? List.of() : itemIds);
        model.addAttribute("addresses", addressRepository.findByUserId(currentUser.id()));
        return "customer/checkout";
    }

    @PostMapping
    public String place(@RequestParam(required = false) Long addressId,
                        @RequestParam(defaultValue = "COD") PaymentMethod method,
                        @RequestParam(required = false) String voucher,
                        @RequestParam(required = false) List<Long> itemIds,
                        HttpServletRequest request, HttpSession session, Model model) {
        List<Order> orders = orderService.placeFromCart(itemIds, addressId, method, voucher);

        if (method == PaymentMethod.VNPAY) {
            session.setAttribute(SESSION_ORDER_IDS, orders.stream().map(Order::getId).toList());
            BigDecimal total = orders.stream().map(Order::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
            String ref = "SS" + System.currentTimeMillis();
            String url = vnpayService.createPaymentUrl(total, ref, "Thanh toan don hang Floria", clientIp(request));
            return "redirect:" + url;
        }
        model.addAttribute("orders", orders);
        return "customer/checkout-result";
    }

    /** VNPAY chuyển hướng về đây sau khi thanh toán (F3.11). */
    @GetMapping("/vnpay-return")
    @SuppressWarnings("unchecked")
    public String vnpayReturn(@RequestParam Map<String, String> params, HttpSession session, Model model) {
        boolean ok = vnpayService.verifyReturn(params);
        if (ok) {
            Object ids = session.getAttribute(SESSION_ORDER_IDS);
            if (ids instanceof List<?> list) orderService.markPaid((List<Long>) list);
            model.addAttribute("note", "Thanh toán VNPAY thành công.");
        } else {
            model.addAttribute("note", "Thanh toán VNPAY chưa hoàn tất — đơn giữ trạng thái chờ thanh toán.");
        }
        session.removeAttribute(SESSION_ORDER_IDS);
        model.addAttribute("orders", List.of());
        return "customer/checkout-result";
    }

    private String clientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        return (ip == null || ip.isBlank()) ? request.getRemoteAddr() : ip.split(",")[0].trim();
    }
}
