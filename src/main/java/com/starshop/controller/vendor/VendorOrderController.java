package com.starshop.controller.vendor;

import com.starshop.domain.Store;
import com.starshop.domain.enums.OrderStatus;
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

/**
 * Controller quản lý Đơn hàng của cửa hàng theo 7 trạng thái và phân công Shipper (F4.4).
 * @owner: TV2 | @leaf: F4.4 Đơn shop (7 trạng thái) | @status: DONE
 */
@Controller
@RequestMapping("/vendor/orders")
@RequiredArgsConstructor
public class VendorOrderController {

    private final VendorOrderService vendorOrderService;
    private final StoreService storeService;
    private final VendorStatsService vendorStatsService;

    @GetMapping
    public String list(@RequestParam(required = false) OrderStatus status, Model model) {
        Store store = storeService.myStore();
        if (store == null) {
            return "redirect:/vendor/register";
        }
        model.addAttribute("store", store);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("statuses", OrderStatus.values());
        model.addAttribute("orders", vendorOrderService.shopOrders(status));
        model.addAttribute("shippers", vendorStatsService.listActiveShippers());
        return "vendor/orders";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam OrderStatus newStatus,
                               RedirectAttributes ra) {
        try {
            vendorOrderService.updateStatus(id, newStatus);
            ra.addFlashAttribute("toast", "Đã cập nhật trạng thái đơn hàng thành công.");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("toastError", ex.getMessage());
        }
        return "redirect:/vendor/orders";
    }

    @PostMapping("/{id}/assign-shipper")
    public String assignShipper(@PathVariable Long id,
                                @RequestParam Long shipperId,
                                RedirectAttributes ra) {
        try {
            vendorOrderService.assignShipper(id, shipperId);
            ra.addFlashAttribute("toast", "Đã phân công Shipper cho đơn hàng thành công.");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("toastError", ex.getMessage());
        }
        return "redirect:/vendor/orders";
    }
}
