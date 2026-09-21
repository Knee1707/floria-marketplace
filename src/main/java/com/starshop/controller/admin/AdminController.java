package com.starshop.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.starshop.service.admin.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Dashboard quản trị toàn hệ thống (/admin = ROLE_ADMIN).
 * Manager dùng chung view qua ManagerController (/manager).
 * @owner: TV3 | @leaf: F6.8–F6.10 Dashboard quản trị | @status: DONE
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final DashboardService dashboardService;
    private final ObjectMapper objectMapper;

    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(@RequestParam(defaultValue = "30") int days, Model model) {
        AdminWeb.dashboardModel(dashboardService.build(days), objectMapper, model);
        return "admin/dashboard";
    }
}
