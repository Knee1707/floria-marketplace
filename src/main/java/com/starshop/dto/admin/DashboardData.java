package com.starshop.dto.admin;

import lombok.Value;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Dữ liệu trang dashboard (F6.8–F6.10). Dùng class + getter (không dùng record)
 * vì JSP EL 5 (Tomcat 10.1) chỉ đọc được JavaBean getter.
 * @owner: TV3
 */
@Value
public class DashboardData {
    /** true = Admin (toàn hệ thống), false = Manager (shop được giao). */
    boolean systemWide;
    int days;
    List<Kpi> kpis;
    Chart chart;
    List<TopStore> topStores;

    /** 1 ô KPI → render bằng &lt;t:stat-tile&gt;. money = định dạng tiền. */
    @Value
    public static class Kpi {
        String label;
        Number value;
        String icon;
        boolean money;
    }

    /** Dữ liệu Chart.js (serialize JSON). Các list cùng độ dài với labels. */
    @Value
    public static class Chart {
        List<String> labels;
        List<BigDecimal> revenue;
        List<Long> orders;
        List<Long> newUsers;   // null với Manager
        List<Long> newStores;  // null với Manager
        Map<String, Long> statusCounts;
        long totalOrders;
    }

    @Value
    public static class TopStore {
        Long id;
        String name;
        BigDecimal revenue;
        long orders;
    }
}
