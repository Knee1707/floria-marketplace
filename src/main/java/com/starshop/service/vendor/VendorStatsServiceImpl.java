package com.starshop.service.vendor;

import com.starshop.domain.Category;
import com.starshop.domain.Commission;
import com.starshop.domain.Order;
import com.starshop.domain.Store;
import com.starshop.domain.StyleValue;
import com.starshop.domain.Transaction;
import com.starshop.domain.User;
import com.starshop.domain.enums.CommonEnums.TransactionType;
import com.starshop.domain.enums.OrderStatus;
import com.starshop.domain.enums.Role;
import com.starshop.repository.CategoryRepository;
import com.starshop.repository.TransactionRepository;
import com.starshop.repository.VendorShipperQueryRepository;
import com.starshop.security.CurrentUser;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Triển khai dịch vụ Ví cửa hàng (F4.6) và Thống kê doanh thu (F4.7).
 * Bám sát rules/database/05-metrics-media.md:
 * revenue (shop) = tổng order.total đơn DELIVERED − hoa hồng app (commission).
 * @owner: TV2 | @leaf: F4.6 Ví shop, F4.7 Doanh thu | @status: DONE
 */
@Service
@RequiredArgsConstructor
public class VendorStatsServiceImpl implements VendorStatsService {

    private static final BigDecimal DEFAULT_COMMISSION_RATE = BigDecimal.valueOf(10); // 10%
    private static final ZoneId VN_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final DateTimeFormatter DAY_FMT = DateTimeFormatter.ofPattern("dd/MM");

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final VendorShipperQueryRepository queryRepository;
    private final StoreService storeService;
    private final CurrentUser currentUser;

    /**
     * Đồng bộ dữ liệu seed (tên hiển thị V6 và tỷ lệ chiết khấu theo cửa hàng trong bảng transactions)
     * mà không cần chỉnh sửa các file migration V1..V6 đã đóng băng checksum.
     */
    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void syncSeedLedgerAndNotifications() {
        queryRepository.syncLegacyNotificationNames();
        queryRepository.syncLegacyTransactionNames();

        List<Store> stores = queryRepository.findAllStores();
        for (Store store : stores) {
            BigDecimal commRate = resolveCommissionRate(store.getId());
            String rateLabel = commRate.stripTrailingZeros().toPlainString();
            List<Order> orders = queryRepository.findOrdersByStoreId(store.getId());
            List<Transaction> txs = queryRepository.findTransactionsByStoreId(store.getId());

            for (Order order : orders) {
                if (order.getStatus() != OrderStatus.DELIVERED) {
                    continue;
                }
                BigDecimal expectedComm = order.getTotal()
                        .multiply(commRate)
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                String expectedCommNote = "Chiết khấu sàn (" + rateLabel + "%) đơn #" + order.getCode();

                Transaction saleTx = txs.stream()
                        .filter(t -> t.getOrder() != null
                                && t.getOrder().getId().equals(order.getId())
                                && t.getType() == TransactionType.SALE)
                        .findFirst()
                        .orElse(null);

                if (saleTx == null) {
                    transactionRepository.save(Transaction.builder()
                            .store(store)
                            .order(order)
                            .type(TransactionType.SALE)
                            .amount(order.getTotal())
                            .note("Doanh thu đơn hàng #" + order.getCode())
                            .build());
                }

                Transaction commTx = txs.stream()
                        .filter(t -> t.getOrder() != null
                                && t.getOrder().getId().equals(order.getId())
                                && t.getType() == TransactionType.COMMISSION)
                        .findFirst()
                        .orElse(null);

                if (commTx == null) {
                    transactionRepository.save(Transaction.builder()
                            .store(store)
                            .order(order)
                            .type(TransactionType.COMMISSION)
                            .amount(expectedComm)
                            .note(expectedCommNote)
                            .build());
                } else if (commTx.getAmount().compareTo(expectedComm) != 0
                        || !expectedCommNote.equals(commTx.getNote())) {
                    commTx.setAmount(expectedComm);
                    commTx.setNote(expectedCommNote);
                    transactionRepository.save(commTx);
                }
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> listCategories() {
        return categoryRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StyleValue> listStyleValues() {
        return queryRepository.findAllStyleValuesWithStyle();
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> listActiveShippers() {
        return queryRepository.findActiveUsersByRole(Role.SHIPPER);
    }

    @Override
    @Transactional(readOnly = true)
    public Store switchActiveStore(Long storeId) {
        if (storeId == null) {
            throw new IllegalArgumentException("Thiếu mã cửa hàng cần chuyển");
        }
        Store store = queryRepository.findStoreByIdAndOwnerId(storeId, currentUser.id())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy cửa hàng hoặc bạn không có quyền quản lý cửa hàng này"));
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attrs) {
            HttpSession session = attrs.getRequest().getSession(true);
            session.setAttribute(StoreServiceImpl.ACTIVE_STORE_SESSION_KEY, store.getId());
        }
        return store;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> myTransactions() {
        Store myStore = requireMyStore();
        return queryRepository.findTransactionsByStoreId(myStore.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal walletBalance() {
        return getRevenueSummary().getWalletBalance();
    }

    @Override
    @Transactional
    public void withdraw(BigDecimal amount, String note) {
        Store myStore = requireMyStore();
        if (amount == null || amount.compareTo(BigDecimal.valueOf(10000)) < 0) {
            throw new IllegalArgumentException("Số tiền rút tối thiểu là 10.000 đ");
        }
        BigDecimal currentBalance = walletBalance();
        if (amount.compareTo(currentBalance) > 0) {
            throw new IllegalArgumentException("Số tiền rút vượt quá số dư khả dụng trong ví (" + currentBalance + " đ)");
        }

        String cleanNote = (note != null && !note.isBlank())
                ? "Rút tiền: " + note.trim()
                : "Yêu cầu rút tiền về tài khoản ngân hàng của shop";

        Transaction tx = Transaction.builder()
                .store(myStore)
                .type(TransactionType.WITHDRAW)
                .amount(amount)
                .note(cleanNote)
                .build();
        transactionRepository.save(tx);
    }

    @Override
    @Transactional(readOnly = true)
    public RevenueSummary getRevenueSummary() {
        Store myStore = requireMyStore();
        long totalProducts = queryRepository.countProductsByStoreId(myStore.getId());
        List<Order> allOrders = queryRepository.findOrdersByStoreId(myStore.getId());
        List<Transaction> transactions = queryRepository.findTransactionsByStoreId(myStore.getId());
        BigDecimal commRate = resolveCommissionRate(myStore.getId());

        long totalOrders = allOrders.size();
        long pendingOrders = allOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.NEW
                        || o.getStatus() == OrderStatus.CONFIRMED
                        || o.getStatus() == OrderStatus.PICKED_UP
                        || o.getStatus() == OrderStatus.SHIPPING)
                .count();

        List<Order> deliveredOrders = allOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED)
                .toList();

        BigDecimal grossRevenue = deliveredOrders.stream()
                .map(Order::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal commissionAmount = grossRevenue.multiply(commRate)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal netRevenue = grossRevenue.subtract(commissionAmount);

        BigDecimal totalWithdrawn = transactions.stream()
                .filter(t -> t.getType() == TransactionType.WITHDRAW)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalRefunded = transactions.stream()
                .filter(t -> t.getType() == TransactionType.REFUND)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal walletBalance = netRevenue.subtract(totalWithdrawn).subtract(totalRefunded);
        if (walletBalance.compareTo(BigDecimal.ZERO) < 0) {
            walletBalance = BigDecimal.ZERO;
        }

        // Thống kê 7 ngày gần nhất cho biểu đồ Chart.js
        LocalDate today = LocalDate.now(VN_ZONE);
        Map<LocalDate, List<Order>> byDay = new LinkedHashMap<>();
        for (int i = 6; i >= 0; i--) {
            byDay.put(today.minusDays(i), new ArrayList<>());
        }
        for (Order o : deliveredOrders) {
            Instant ts = o.getUpdatedAt() != null ? o.getUpdatedAt()
                    : (o.getCreatedAt() != null ? o.getCreatedAt() : Instant.now());
            LocalDate d = ts.atZone(VN_ZONE).toLocalDate();
            if (byDay.containsKey(d)) {
                byDay.get(d).add(o);
            }
        }

        List<DailyRevenuePoint> points = new ArrayList<>();
        for (Map.Entry<LocalDate, List<Order>> entry : byDay.entrySet()) {
            String label = entry.getKey().format(DAY_FMT);
            List<Order> dayOrders = entry.getValue();
            BigDecimal dayGross = dayOrders.stream()
                    .map(Order::getTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal dayComm = dayGross.multiply(commRate)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            BigDecimal dayNet = dayGross.subtract(dayComm);

            points.add(DailyRevenuePoint.builder()
                    .label(label)
                    .orderCount(dayOrders.size())
                    .gross(dayGross)
                    .commission(dayComm)
                    .net(dayNet)
                    .build());
        }

        String labelsJson = "[" + points.stream()
                .map(p -> "\"" + p.getLabel() + "\"")
                .collect(Collectors.joining(",")) + "]";
        String grossJson = "[" + points.stream()
                .map(p -> p.getGross().toPlainString())
                .collect(Collectors.joining(",")) + "]";
        String netJson = "[" + points.stream()
                .map(p -> p.getNet().toPlainString())
                .collect(Collectors.joining(",")) + "]";

        return RevenueSummary.builder()
                .totalProducts(totalProducts)
                .totalOrders(totalOrders)
                .pendingOrders(pendingOrders)
                .deliveredOrders(deliveredOrders.size())
                .grossRevenue(grossRevenue)
                .commissionRate(commRate)
                .commissionAmount(commissionAmount)
                .netRevenue(netRevenue)
                .walletBalance(walletBalance)
                .totalWithdrawn(totalWithdrawn)
                .chartLabelsJson(labelsJson)
                .chartGrossJson(grossJson)
                .chartNetJson(netJson)
                .dailyPoints(points)
                .build();
    }

    private BigDecimal resolveCommissionRate(Long storeId) {
        List<Commission> commissions = queryRepository.findApplicableCommissions(storeId);
        // Ưu tiên chiết khấu gán riêng cho storeId, nếu không có thì lấy chiết khấu chung (store == null)
        for (Commission c : commissions) {
            if (c.getStore() != null && c.getStore().getId().equals(storeId)) {
                return c.getRate();
            }
        }
        for (Commission c : commissions) {
            if (c.getStore() == null) {
                return c.getRate();
            }
        }
        return DEFAULT_COMMISSION_RATE;
    }

    private Store requireMyStore() {
        Store store = storeService.myStore();
        if (store == null) {
            throw new IllegalStateException("Bạn chưa đăng ký cửa hàng");
        }
        return store;
    }
}
