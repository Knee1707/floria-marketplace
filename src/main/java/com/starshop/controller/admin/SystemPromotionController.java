package com.starshop.controller.admin;

import com.starshop.domain.enums.CommonEnums.PromotionType;
import com.starshop.dto.admin.AdminForms.PromotionForm;
import com.starshop.dto.admin.AdminForms.VoucherForm;
import com.starshop.service.admin.SystemPromotionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * F6.6 Khuyến mãi hệ thống (voucher + chương trình, store_id = null). Chỉ Admin.
 * Tên SystemPromotionController để không trùng bean với vendor/PromotionController của TV2.
 * @owner: TV3 | @leaf: F6.6 | @status: DONE
 */
@Controller
@RequestMapping("/admin/promotions")
@RequiredArgsConstructor
public class SystemPromotionController {

    private final SystemPromotionService promotionService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("vouchers", promotionService.vouchers());
        model.addAttribute("promotions", promotionService.promotions());
        model.addAttribute("types", PromotionType.values());
        return "admin/promotions";
    }

    @PostMapping("/vouchers")
    public String createVoucher(@Valid @ModelAttribute VoucherForm form, BindingResult br, RedirectAttributes ra) {
        AdminWeb.requireValid(br);
        promotionService.createVoucher(form);
        AdminWeb.ok(ra, "Đã tạo voucher");
        return "redirect:/admin/promotions";
    }

    @PostMapping("/vouchers/{id}/active")
    public String setVoucherActive(@PathVariable Long id, @RequestParam boolean active, RedirectAttributes ra) {
        promotionService.setVoucherActive(id, active);
        AdminWeb.ok(ra, active ? "Đã bật voucher" : "Đã tắt voucher");
        return "redirect:/admin/promotions";
    }

    @PostMapping("/programs")
    public String createPromotion(@Valid @ModelAttribute PromotionForm form, BindingResult br, RedirectAttributes ra) {
        AdminWeb.requireValid(br);
        promotionService.createPromotion(form);
        AdminWeb.ok(ra, "Đã tạo chương trình khuyến mãi");
        return "redirect:/admin/promotions";
    }

    @PostMapping("/programs/{id}/delete")
    public String deletePromotion(@PathVariable Long id, RedirectAttributes ra) {
        promotionService.deletePromotion(id);
        AdminWeb.ok(ra, "Đã xoá chương trình khuyến mãi");
        return "redirect:/admin/promotions";
    }
}
