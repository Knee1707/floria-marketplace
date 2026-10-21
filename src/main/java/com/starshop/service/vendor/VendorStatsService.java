package com.starshop.service.vendor;

import com.starshop.domain.Category;
import com.starshop.domain.Store;
import com.starshop.domain.StyleValue;
import com.starshop.domain.Transaction;
import com.starshop.domain.User;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

/**
 * Hợp đồng dịch vụ Ví cửa hàng (F4.6), Thống kê doanh thu (F4.7) và tra cứu danh mục/shipper cho kênh Vendor.
 * @owner: TV2 | @leaf: F4.6, F4.7 | @status: DONE
 */
public interface VendorStatsService {

    /** Lấy danh sách giao dịch ví của cửa hàng hiện tại. */
    List<Transaction> myTransactions();

    /** Tính số dư khả dụng trong ví cửa hàng. */
    BigDecimal walletBalance();

    /** Tạo giao dịch rút tiền khỏi ví cửa hàng (F4.6). */
    void withdraw(BigDecimal amount, String note);

    /** Lấy dữ liệu tổng hợp KPI & biểu đồ doanh thu (F4.2, F4.7) — mặc định 7 ngày gần nhất cho biểu đồ, KPI toàn thời gian. */
    RevenueSummary getRevenueSummary();

    /**
     * Thống kê doanh thu THEO KHOẢNG NGÀY [from, to] (F4.7): KPI (gộp/ròng/số đơn giao) và biểu đồ/bảng
     * đều tính trong khoảng đã chọn. Dùng cho bộ lọc ngày ở trang báo cáo doanh thu.
     */
    RevenueSummary getRevenueSummary(java.time.LocalDate from, java.time.LocalDate to);

    /** Lấy danh sách danh mục hoa cho form sản phẩm (F4.3). */
    List<Category> listCategories();

    /** Lấy danh sách giá trị thuộc tính hoa do Admin cấu hình (F4.3). */
    List<StyleValue> listStyleValues();

    /** Lấy danh sách Shipper đang hoạt động để phân công đơn hàng (F4.4). */
    List<User> listActiveShippers();

    /** Chuyển đổi cửa hàng đang quản lý khi Vendor sở hữu nhiều shop (F4.2). */
    Store switchActiveStore(Long storeId);

    @Getter
    @Builder
    class RevenueSummary {
        private final long totalProducts;
        private final long totalOrders;
        private final long pendingOrders;
        private final long deliveredOrders;
        private final BigDecimal grossRevenue;
        private final BigDecimal commissionRate;
        private final BigDecimal commissionAmount;
        private final BigDecimal netRevenue;
        private final BigDecimal walletBalance;
        private final BigDecimal totalWithdrawn;
        private final String chartLabelsJson;
        private final String chartGrossJson;
        private final String chartNetJson;
        private final List<DailyRevenuePoint> dailyPoints;
    }

    @Getter
    @Builder
    class DailyRevenuePoint {
        private final String label;
        private final long orderCount;
        private final BigDecimal gross;
        private final BigDecimal commission;
        private final BigDecimal net;
    }
}
