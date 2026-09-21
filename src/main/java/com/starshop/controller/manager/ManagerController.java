package com.starshop.controller.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.starshop.controller.admin.AdminWeb;
import com.starshop.service.admin.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Khu Manager (/manager/** = ADMIN|MANAGER): quản lý các shop được phân công trong store_managers.
 * Dashboard dùng chung view admin/dashboard; DashboardService tự giới hạn phạm vi theo AdminScope.
 * Các trang shop / sản phẩm / chiết khấu dùng chung controller trong controller/admin (map cả /manager/**).
 * @owner: TV3 | @leaf: F6.x (phạm vi shop được giao) | @status: DONE
 */
@Controller
@RequestMapping("/manager")
@RequiredArgsConstructor
public class ManagerController {

    private final DashboardService dashboardService;
    private final ObjectMapper objectMapper;

    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(@RequestParam(defaultValue = "30") int days, Model model) {
        AdminWeb.dashboardModel(dashboardService.build(days), objectMapper, model);
        return "admin/dashboard";
    }
}
