package com.starshop.controller.admin;

import com.starshop.dto.admin.AdminForms.DeliveryForm;
import com.starshop.service.admin.DeliveryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * F6.7 Nhà vận chuyển (tên + phí ship). Admin + Manager (cấu hình dùng chung cả chuỗi).
 * @owner: TV3 | @leaf: F6.7 | @status: DONE
 */
@Controller("adminDeliveryController") // tên bean riêng, tránh trùng nếu nơi khác có lớp cùng tên
@RequestMapping({"/admin/deliveries", "/manager/deliveries"})
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("deliveries", deliveryService.all());
        return "admin/deliveries";
    }

    @PostMapping
    public String save(@Valid @ModelAttribute DeliveryForm form, BindingResult br,
                       HttpServletRequest req, RedirectAttributes ra) {
        AdminWeb.requireValid(br);
        deliveryService.save(form.id(), form.name(), form.shippingFee());
        AdminWeb.ok(ra, form.id() == null ? "Đã thêm nhà vận chuyển" : "Đã cập nhật nhà vận chuyển");
        return "redirect:/" + AdminWeb.area(req) + "/deliveries";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, HttpServletRequest req, RedirectAttributes ra) {
        deliveryService.delete(id);
        AdminWeb.ok(ra, "Đã xoá nhà vận chuyển");
        return "redirect:/" + AdminWeb.area(req) + "/deliveries";
    }
}
