package com.starshop.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * ================= STUB cho TV3 (Admin + Manager - D6) =================
 * /admin/** = ROLE_ADMIN, /manager/** = ADMIN|MANAGER. TV3 điền F6.1 -> F6.10.
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    // @owner: TV3 | @leaf: F6.x Dashboard quản trị | @status: TODO
    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(Model model) {
        model.addAttribute("owner", "TV3");
        model.addAttribute("area", "Admin / Quản trị (D6)");
        model.addAttribute("todo", "F6.1 Quản lý user, F6.2 Danh mục, F6.3 Thuộc tính SP, F6.4 Duyệt shop/SP, "
                + "F6.5 Commission, F6.6 KM hệ thống, F6.7 Nhà vận chuyển, F6.8-6.10 Thống kê/Dashboard");
        return "common/stub";
    }
}
