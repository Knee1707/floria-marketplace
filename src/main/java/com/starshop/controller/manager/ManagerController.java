package com.starshop.controller.manager;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * ================= STUB cho TV3 (Manager - D6) =================
 * /manager/** = ADMIN|MANAGER. Manager thao tác trên shop được phân công (store_managers).
 */
@Controller
@RequestMapping("/manager")
public class ManagerController {

    // @owner: TV3 | @leaf: F6.x (phạm vi 1 shop) | @status: TODO
    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(Model model) {
        model.addAttribute("owner", "TV3");
        model.addAttribute("area", "Manager / Quản lý cửa hàng được phân công (D6)");
        model.addAttribute("todo", "Như Admin nhưng giới hạn ở shop trong store_managers");
        return "common/stub";
    }
}
