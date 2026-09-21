package com.starshop.service.admin;

import com.starshop.dto.admin.DashboardData;

/**
 * F6.8–F6.10 Dashboard thống kê theo thời gian (Chart.js).
 * Admin = toàn hệ thống; Manager = các shop được phân công (tự xác định qua AdminScope).
 * @owner: TV3
 */
public interface DashboardService {
    /** @param days số ngày gần nhất (7/30/90). */
    DashboardData build(int days);
}
