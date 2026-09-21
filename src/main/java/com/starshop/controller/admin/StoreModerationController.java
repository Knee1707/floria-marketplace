package com.starshop.controller.admin;

import com.starshop.service.admin.ModerationService;
import com.starshop.service.admin.UserAdminService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * F6.4 Duyệt / cấm cửa hàng. Admin: mọi shop; Manager: shop được phân công (service kiểm phạm vi).
 * @owner: TV3 | @leaf: F6.4 | @status: DONE
 */
@Controller
@RequestMapping({"/admin/stores", "/manager/stores"})
@RequiredArgsConstructor
public class StoreModerationController {

    private final ModerationService moderationService;
    private final UserAdminService userAdminService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("stores", moderationService.stores());
        return "admin/stores";
    }

    @PostMapping("/{id}/approve")
    public String approve(@PathVariable Long id, @RequestParam boolean approved,
                          HttpServletRequest req, RedirectAttributes ra) {
        userAdminService.setStoreApproved(id, approved);
        AdminWeb.ok(ra, approved ? "Đã duyệt cửa hàng" : "Đã cấm cửa hàng");
        return "redirect:/" + AdminWeb.area(req) + "/stores";
    }
}
