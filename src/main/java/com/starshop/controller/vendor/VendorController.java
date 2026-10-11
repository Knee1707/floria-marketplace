package com.starshop.controller.vendor;

import com.starshop.domain.Order;
import com.starshop.domain.Store;
import com.starshop.security.CurrentUser;
import com.starshop.service.common.NotificationService;
import com.starshop.service.vendor.StoreService;
import com.starshop.service.vendor.VendorOrderService;
import com.starshop.service.vendor.VendorStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controller chính của Kênh Người Bán (Vendor - D4):
 * F4.2 Trang chủ/Dashboard shop, F4.6 Ví shop, F4.7 Doanh thu & Chart.js, F7.1 Thông báo.
 * @owner: TV2 | @leaf: F4.2, F4.6, F4.7, F7.1 | @status: DONE
 */
@Controller
@RequestMapping("/vendor")
@RequiredArgsConstructor
public class VendorController {

    private final StoreService storeService;
    private final VendorOrderService vendorOrderService;
    private final VendorStatsService vendorStatsService;
    private final NotificationService notificationService;
    private final CurrentUser currentUser;

    // @owner: TV2 | @leaf: F4.2 Trang chủ / Dashboard shop | @status: DONE
    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(Model model) {
        Store store = storeService.myStore();
        if (store == null) {
            return "redirect:/vendor/register";
        }
        List<Order> recentOrders = vendorOrderService.shopOrders(null).stream().limit(5).toList();
        model.addAttribute("store", store);
        model.addAttribute("summary", vendorStatsService.getRevenueSummary());
        model.addAttribute("recentOrders", recentOrders);
        model.addAttribute("notifications", notificationService.myNotifications().stream().limit(8).toList());
        model.addAttribute("unreadCount", notificationService.unreadCount(currentUser.id()));
        return "vendor/dashboard";
    }

    // @owner: TV2 | @leaf: F4.6 Ví shop | @status: DONE
    @GetMapping("/wallet")
    public String wallet(Model model) {
        Store store = storeService.myStore();
        if (store == null) {
            return "redirect:/vendor/register";
        }
        model.addAttribute("store", store);
        model.addAttribute("summary", vendorStatsService.getRevenueSummary());
        model.addAttribute("transactions", vendorStatsService.myTransactions());
        return "vendor/wallet";
    }

    // @owner: TV2 | @leaf: F4.6 Rút tiền ví shop | @status: DONE
    @PostMapping("/wallet/withdraw")
    public String withdraw(@RequestParam BigDecimal amount,
                           @RequestParam(required = false) String note,
                           RedirectAttributes ra) {
        try {
            vendorStatsService.withdraw(amount, note);
            ra.addFlashAttribute("toast", "Đã tạo lệnh rút tiền thành công.");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("toastError", ex.getMessage());
        }
        return "redirect:/vendor/wallet";
    }

    // @owner: TV2 | @leaf: F4.7 Thống kê doanh thu (Chart.js) | @status: DONE
    @GetMapping("/revenue")
    public String revenue(Model model) {
        Store store = storeService.myStore();
        if (store == null) {
            return "redirect:/vendor/register";
        }
        model.addAttribute("store", store);
        model.addAttribute("summary", vendorStatsService.getRevenueSummary());
        return "vendor/revenue";
    }

    // @owner: TV2 | @leaf: F7.1 Đánh dấu đã đọc thông báo | @status: DONE
    @PostMapping("/notifications/{id}/read")
    public String markNotificationRead(@PathVariable Long id, RedirectAttributes ra) {
        notificationService.markRead(id);
        ra.addFlashAttribute("toast", "Đã đánh dấu thông báo là đã đọc.");
        return "redirect:/vendor";
    }
}
