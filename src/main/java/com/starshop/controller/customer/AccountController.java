package com.starshop.controller.customer;

import com.starshop.domain.UserAddress;
import com.starshop.domain.enums.OrderStatus;
import com.starshop.repository.UserAddressRepository;
import com.starshop.security.CurrentUser;
import com.starshop.service.customer.OrderService;
import com.starshop.service.customer.ViewHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/** Tài khoản: hồ sơ, địa chỉ, lịch sử đơn (F3.12–F3.14). @owner: L. */
@Controller
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private final OrderService orderService;
    private final ViewHistoryService viewHistoryService;
    private final UserAddressRepository addressRepository;
    private final CurrentUser currentUser;

    @GetMapping("/profile")
    public String profile(Model model) {
        model.addAttribute("user", currentUser.get());
        return "customer/profile";
    }

    @GetMapping("/orders")
    public String orders(@RequestParam(required = false) OrderStatus status,
                         @RequestParam(defaultValue = "0") int page, Model model) {
        var pageable = PageRequest.of(Math.max(page, 0), 10, Sort.by("createdAt").descending());
        model.addAttribute("orders", orderService.myOrders(status, pageable));
        model.addAttribute("status", status);
        model.addAttribute("statuses", OrderStatus.values());
        return "customer/order-history";
    }

    @GetMapping("/viewed")
    public String viewed(Model model) {
        model.addAttribute("items", viewHistoryService.myRecent());
        return "customer/viewed";
    }

    @GetMapping("/addresses")
    public String addresses(Model model) {
        model.addAttribute("addresses", addressRepository.findByUserId(currentUser.id()));
        return "customer/addresses";
    }

    @PostMapping("/addresses")
    public String addAddress(@RequestParam String receiverName, @RequestParam String phone,
                             @RequestParam String addressLine, @RequestParam(required = false) String ward,
                             @RequestParam(required = false) String district, @RequestParam(required = false) String province) {
        addressRepository.save(UserAddress.builder()
                .user(currentUser.get()).receiverName(receiverName).phone(phone)
                .addressLine(addressLine).ward(ward).district(district).province(province)
                .isDefault(addressRepository.findByUserId(currentUser.id()).isEmpty())
                .build());
        return "redirect:/account/addresses";
    }

    @PostMapping("/addresses/delete")
    public String deleteAddress(@RequestParam Long id) {
        addressRepository.findById(id)
                .filter(a -> a.getUser().getId().equals(currentUser.id()))
                .ifPresent(addressRepository::delete);
        return "redirect:/account/addresses";
    }
}
