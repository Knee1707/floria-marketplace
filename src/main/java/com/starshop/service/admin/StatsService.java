package com.starshop.service.admin;

import java.math.BigDecimal;

/**
 * HỢP ĐỒNG — TV3 viết StatsServiceImpl (@owner: TV3 | F6.8–F6.10).
 * Metric định nghĩa ở rules/database/05-metrics-media.md. Dữ liệu vẽ Chart.js.
 */
public interface StatsService {
    BigDecimal totalRevenue();   // tổng order.total các đơn DELIVERED
    long totalUsers();
    long totalStores();
    long ordersByToday();
}
