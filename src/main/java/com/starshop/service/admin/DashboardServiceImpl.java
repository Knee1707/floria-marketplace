package com.starshop.service.admin;

import com.starshop.domain.enums.CommonEnums.ProductStatus;
import com.starshop.domain.enums.OrderStatus;
import com.starshop.dto.admin.DashboardData;
import com.starshop.dto.admin.DashboardData.Chart;
import com.starshop.dto.admin.DashboardData.Kpi;
import com.starshop.dto.admin.DashboardData.TopStore;
import com.starshop.repository.AdminStatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

/** @owner: TV3 | @leaf: F6.8–F6.10 Thống kê/Dashboard | @status: DONE */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private static final Set<Integer> ALLOWED_DAYS = Set.of(7, 30, 90);
    private static final int TOP_STORES = 5;

    private final AdminScope adminScope;
    private final StatsService statsService;
    private final AdminStatsRepository statsRepository;

    @Override
    public DashboardData build(int days) {
        int range = ALLOWED_DAYS.contains(days) ? days : 30;
        AdminScope.StoreScope scope = adminScope.current();

        LocalDate today = LocalDate.now(StatsServiceImpl.VN);
        LocalDate firstDay = today.minusDays(range - 1L);
        Instant from = firstDay.atStartOfDay(StatsServiceImpl.VN).toInstant();

        // Manager chưa được phân công shop nào → dữ liệu rỗng (không query với IN ()).
        boolean none = scope.isEmpty();
        Set<Long> ids = scope.storeIds();

        List<Kpi> kpis = new ArrayList<>();
        if (scope.systemWide()) {
            kpis.add(new Kpi("Doanh thu đơn đã giao", statsService.totalRevenue(), "chart", true));
            kpis.add(new Kpi("Người dùng", statsService.totalUsers(), "user", false));
            kpis.add(new Kpi("Cửa hàng", statsService.totalStores(), "store", false));
            kpis.add(new Kpi("Đơn hôm nay", statsService.ordersByToday(), "package", false));
            kpis.add(new Kpi("Shop chờ duyệt", statsRepository.countPendingStores(), "check", false));
        } else {
            kpis.add(new Kpi("Doanh thu đơn đã giao",
                    none ? BigDecimal.ZERO : statsRepository.sumTotalByStatusInStores(OrderStatus.DELIVERED, ids), "chart", true));
            kpis.add(new Kpi("Shop phụ trách", ids.size(), "store", false));
            kpis.add(new Kpi("Đơn hôm nay",
                    none ? 0L : statsRepository.countOrdersSinceInStores(StatsServiceImpl.startOfToday(), ids), "package", false));
            kpis.add(new Kpi("SP đang bán",
                    none ? 0L : statsRepository.countProductsInStoresByStatus(ids, ProductStatus.ACTIVE), "tag", false));
        }

        // Chuỗi theo ngày — lấp đủ mọi ngày (ngày không có đơn = 0) để trục X liên tục.
        List<Object[]> daily = none ? List.of()
                : scope.systemWide() ? statsRepository.dailyOrders(from)
                : statsRepository.dailyOrdersInStores(from, ids);
        Map<String, Object[]> dailyByDay = new HashMap<>();
        for (Object[] r : daily) dailyByDay.put((String) r[0], r);

        List<String> labels = new ArrayList<>(range);
        List<BigDecimal> revenue = new ArrayList<>(range);
        List<Long> orders = new ArrayList<>(range);
        for (LocalDate d = firstDay; !d.isAfter(today); d = d.plusDays(1)) {
            String key = d.toString(); // YYYY-MM-DD, khớp to_char ở repository
            labels.add(key);
            Object[] r = dailyByDay.get(key);
            revenue.add(r == null ? BigDecimal.ZERO : toBigDecimal(r[1]));
            orders.add(r == null ? 0L : ((Number) r[2]).longValue());
        }

        List<Long> newUsers = null;
        List<Long> newStores = null;
        if (scope.systemWide()) {
            newUsers = fill(labels, statsRepository.dailyNewUsers(from));
            newStores = fill(labels, statsRepository.dailyNewStores(from));
        }

        // Phân bố trạng thái đơn — giữ đúng thứ tự vòng đời enum.
        Map<String, Long> statusCounts = new LinkedHashMap<>();
        for (OrderStatus s : OrderStatus.values()) statusCounts.put(s.name(), 0L);
        List<Object[]> byStatus = none ? List.of()
                : scope.systemWide() ? statsRepository.countByStatusSince(from)
                : statsRepository.countByStatusSinceInStores(from, ids);
        long totalOrders = 0;
        for (Object[] r : byStatus) {
            long c = ((Number) r[1]).longValue();
            statusCounts.put(String.valueOf(r[0]), c);
            totalOrders += c;
        }

        var top = PageRequest.of(0, TOP_STORES);
        List<Object[]> topRows = none ? List.of()
                : scope.systemWide() ? statsRepository.topStoresSince(OrderStatus.DELIVERED, from, top)
                : statsRepository.topStoresSinceInStores(OrderStatus.DELIVERED, from, ids, top);
        List<TopStore> topStores = topRows.stream()
                .map(r -> new TopStore(((Number) r[0]).longValue(), (String) r[1],
                        toBigDecimal(r[2]), ((Number) r[3]).longValue()))
                .toList();

        Chart chart = new Chart(labels, revenue, orders, newUsers, newStores, statusCounts, totalOrders);
        return new DashboardData(scope.systemWide(), range, kpis, chart, topStores);
    }

    private static List<Long> fill(List<String> labels, List<Object[]> rows) {
        Map<String, Long> m = new HashMap<>();
        for (Object[] r : rows) m.put((String) r[0], ((Number) r[1]).longValue());
        return labels.stream().map(l -> m.getOrDefault(l, 0L)).toList();
    }

    private static BigDecimal toBigDecimal(Object o) {
        if (o == null) return BigDecimal.ZERO;
        if (o instanceof BigDecimal b) return b;
        return new BigDecimal(o.toString());
    }
}
