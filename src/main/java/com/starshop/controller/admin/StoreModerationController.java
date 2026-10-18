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
 * Admin còn phân công / gỡ Manager phụ trách từng shop (store_managers).
 * @owner: TV3 | @leaf: F6.4 | @status: DONE
 */
@Controller
@RequestMapping({"/admin/stores", "/manager/stores"})
@RequiredArgsConstructor
public class StoreModerationController {

    private final ModerationService moderationService;
    private final UserAdminService userAdminService;

    @GetMapping
    public String list(HttpServletRequest req, Model model) {
        var stores = moderationService.stores();
        model.addAttribute("stores", stores);
        model.addAttribute("managersByStore", moderationService.managersByStore(stores));
        if (AdminWeb.AREA_ADMIN.equals(AdminWeb.area(req))) {
            model.addAttribute("managerCandidates", moderationService.managerCandidates());
        }
        return "admin/stores";
    }

    @PostMapping("/{id}/approve")
    public String approve(@PathVariable Long id, @RequestParam boolean approved,
                          HttpServletRequest req, RedirectAttributes ra) {
        userAdminService.setStoreApproved(id, approved);
        AdminWeb.ok(ra, approved ? "Đã duyệt cửa hàng" : "Đã cấm cửa hàng");
        return "redirect:/" + AdminWeb.area(req) + "/stores";
    }

    /** Admin phân công Manager cho shop (service chặn Manager tự gọi). */
    @PostMapping("/{id}/managers")
    public String assignManager(@PathVariable Long id, @RequestParam Long managerId,
                                HttpServletRequest req, RedirectAttributes ra) {
        moderationService.assignManager(id, managerId);
        AdminWeb.ok(ra, "Đã phân công Manager cho cửa hàng");
        return "redirect:/" + AdminWeb.area(req) + "/stores";
    }

    @PostMapping("/{id}/managers/{managerId}/delete")
    public String unassignManager(@PathVariable Long id, @PathVariable Long managerId,
                                  HttpServletRequest req, RedirectAttributes ra) {
        moderationService.unassignManager(id, managerId);
        AdminWeb.ok(ra, "Đã gỡ Manager khỏi cửa hàng");
        return "redirect:/" + AdminWeb.area(req) + "/stores";
    }
}
