package com.starshop.service.admin;

import com.starshop.domain.enums.OrderStatus;
import com.starshop.repository.AdminStatsRepository;
import com.starshop.repository.StoreRepository;
import com.starshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * KPI toàn hệ thống (hợp đồng StatsService). Metric: rules/database/05-metrics-media.md.
 * @owner: TV3 | @leaf: F6.8–F6.10 | @status: DONE
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {

    static final ZoneId VN = ZoneId.of("Asia/Ho_Chi_Minh");

    private final AdminStatsRepository statsRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;

    @Override
    public BigDecimal totalRevenue() {
        return statsRepository.sumTotalByStatus(OrderStatus.DELIVERED);
    }

    @Override
    public long totalUsers() {
        return userRepository.count();
    }

    @Override
    public long totalStores() {
        return storeRepository.count();
    }

    @Override
    public long ordersByToday() {
        return statsRepository.countOrdersSince(startOfToday());
    }

    /** 00:00 hôm nay theo giờ Việt Nam. */
    static Instant startOfToday() {
        return LocalDate.now(VN).atStartOfDay(VN).toInstant();
    }
}
