package com.starshop.controller.vendor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * ================= STUB cho TV2 (Vendor - D4) =================
 * Leader đã dựng route + phân quyền (/vendor/** = ROLE_VENDOR).
 * TV2 điền body từng leaf F4.1 -> F4.7, tạo thêm controller/service tương ứng.
 */
@Controller
@RequestMapping("/vendor")
public class VendorController {

    // @owner: TV2 | @leaf: F4.x Dashboard shop | @status: TODO
    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(Model model) {
        model.addAttribute("owner", "TV2");
        model.addAttribute("area", "Vendor / Bán hàng (D4)");
        model.addAttribute("todo", "F4.1 Đăng ký shop, F4.2 Trang chủ shop, F4.3 CRUD SP, "
                + "F4.4 Đơn shop, F4.5 Khuyến mãi, F4.6 Ví, F4.7 Doanh thu");
        return "common/stub";
    }
}
