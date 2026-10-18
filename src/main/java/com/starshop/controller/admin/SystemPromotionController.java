package com.starshop.controller.admin;

import com.starshop.domain.enums.CommonEnums.PromotionType;
import com.starshop.dto.admin.AdminForms.PromotionForm;
import com.starshop.dto.admin.AdminForms.VoucherForm;
import com.starshop.service.admin.ModerationService;
import com.starshop.service.admin.SystemPromotionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * F6.6 Khuyến mãi (voucher + chương trình: giảm % sản phẩm / giảm phí vận chuyển).
 * Admin: toàn sàn + mọi shop. Manager: shop được phân công (service kiểm phạm vi).
 * Tên SystemPromotionController để không trùng bean với vendor/PromotionController của TV2.
 * @owner: TV3 | @leaf: F6.6 | @status: DONE
 */
@Controller
@RequestMapping({"/admin/promotions", "/manager/promotions"})
@RequiredArgsConstructor
public class SystemPromotionController {

    private final SystemPromotionService promotionService;
    private final ModerationService moderationService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("vouchers", promotionService.vouchers());
        model.addAttribute("promotions", promotionService.promotions());
        model.addAttribute("types", PromotionType.values());
        model.addAttribute("stores", moderationService.stores()); // shop trong phạm vi để chọn khi tạo
        return "admin/promotions";
    }

    @PostMapping("/vouchers")
    public String createVoucher(@Valid @ModelAttribute VoucherForm form, BindingResult br,
                                HttpServletRequest req, RedirectAttributes ra) {
        AdminWeb.requireValid(br);
        promotionService.createVoucher(form);
        AdminWeb.ok(ra, "Đã tạo voucher");
        return redirect(req);
    }

    @PostMapping("/vouchers/{id}/active")
    public String setVoucherActive(@PathVariable Long id, @RequestParam boolean active,
                                   HttpServletRequest req, RedirectAttributes ra) {
        promotionService.setVoucherActive(id, active);
        AdminWeb.ok(ra, active ? "Đã bật voucher" : "Đã tắt voucher");
        return redirect(req);
    }

    @PostMapping("/programs")
    public String createPromotion(@Valid @ModelAttribute PromotionForm form, BindingResult br,
                                  HttpServletRequest req, RedirectAttributes ra) {
        AdminWeb.requireValid(br);
        promotionService.createPromotion(form);
        AdminWeb.ok(ra, "Đã tạo chương trình khuyến mãi");
        return redirect(req);
    }

    @PostMapping("/programs/{id}/delete")
    public String deletePromotion(@PathVariable Long id, HttpServletRequest req, RedirectAttributes ra) {
        promotionService.deletePromotion(id);
        AdminWeb.ok(ra, "Đã xoá chương trình khuyến mãi");
        return redirect(req);
    }

    private static String redirect(HttpServletRequest req) {
        return "redirect:/" + AdminWeb.area(req) + "/promotions";
    }
}
