package com.starshop.controller.shipper;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * ================= STUB cho TV2 (Shipper - D5) =================
 * /shipper/** = ROLE_SHIPPER. TV2 điền F5.1 -> F5.3.
 */
@Controller
@RequestMapping("/shipper")
public class ShipperController {

    // @owner: TV2 | @leaf: F5.x | @status: TODO
    @GetMapping({"", "/", "/orders"})
    public String assignedOrders(Model model) {
        model.addAttribute("owner", "TV2");
        model.addAttribute("area", "Shipper / Giao vận (D5)");
        model.addAttribute("todo", "F5.1 Đơn được phân công, F5.2 Cập nhật trạng thái giao, F5.3 Thống kê");
        return "common/stub";
    }
}
