package com.starshop.controller.vendor;

import com.starshop.domain.Store;
import com.starshop.domain.enums.CommonEnums.PromotionType;
import com.starshop.service.vendor.PromotionService;
import com.starshop.service.vendor.StoreService;
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

/**
 * Controller quản lý Khuyến mãi / Voucher của cửa hàng (F4.5).
 * @owner: TV2 | @leaf: F4.5 Khuyến mãi | @status: DONE
 */
@Controller
@RequestMapping("/vendor/promotions")
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService promotionService;
    private final StoreService storeService;

    @GetMapping
    public String list(Model model) {
        Store store = storeService.myStore();
        if (store == null) {
            return "redirect:/vendor/register";
        }
        model.addAttribute("store", store);
        model.addAttribute("vouchers", promotionService.myVouchers());
        model.addAttribute("promotionTypes", PromotionType.values());
        return "vendor/promotions";
    }

    @PostMapping("/create")
    public String create(@RequestParam String code,
                         @RequestParam PromotionType type,
                         @RequestParam BigDecimal value,
                         @RequestParam(required = false) BigDecimal minOrder,
                         @RequestParam(required = false) BigDecimal maxDiscount,
                         @RequestParam(required = false) Integer quota,
                         RedirectAttributes ra) {
        try {
            promotionService.createVoucher(code, type, value, minOrder, maxDiscount, quota);
            ra.addFlashAttribute("toast", "Đã tạo mã giảm giá \"" + code.toUpperCase() + "\" thành công.");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("toastError", ex.getMessage());
        }
        return "redirect:/vendor/promotions";
    }

    @PostMapping("/{id}/toggle")
    public String toggle(@PathVariable Long id,
                         @RequestParam boolean active,
                         RedirectAttributes ra) {
        try {
            promotionService.toggleActive(id, active);
            ra.addFlashAttribute("toast", active ? "Đã kích hoạt mã giảm giá." : "Đã tạm ngưng mã giảm giá.");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("toastError", ex.getMessage());
        }
        return "redirect:/vendor/promotions";
    }
}
