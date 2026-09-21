package com.starshop.controller.admin;

import com.starshop.dto.admin.AdminForms.CommissionForm;
import com.starshop.service.admin.CommissionService;
import com.starshop.service.admin.ModerationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * F6.5 Chiết khấu app. Admin: mức mặc định + mọi shop; Manager: shop được giao.
 * @owner: TV3 | @leaf: F6.5 | @status: DONE
 */
@Controller("adminCommissionController") // tên bean riêng, tránh trùng nếu nơi khác có lớp cùng tên
@RequestMapping({"/admin/commissions", "/manager/commissions"})
@RequiredArgsConstructor
public class CommissionController {

    private final CommissionService commissionService;
    private final ModerationService moderationService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("commissions", commissionService.all());
        model.addAttribute("stores", moderationService.stores()); // chỉ shop trong phạm vi
        return "admin/commissions";
    }

    @PostMapping
    public String save(@Valid @ModelAttribute CommissionForm form, BindingResult br,
                       HttpServletRequest req, RedirectAttributes ra) {
        AdminWeb.requireValid(br);
        commissionService.save(form.id(), form.storeId(), form.rate(), form.note());
        AdminWeb.ok(ra, "Đã lưu chiết khấu");
        return "redirect:/" + AdminWeb.area(req) + "/commissions";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, HttpServletRequest req, RedirectAttributes ra) {
        commissionService.delete(id);
        AdminWeb.ok(ra, "Đã xoá chiết khấu");
        return "redirect:/" + AdminWeb.area(req) + "/commissions";
    }
}
