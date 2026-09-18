package com.starshop.service.customer;

import com.starshop.domain.ProductViewHistory;
import java.util.List;

/** Sản phẩm đã xem (F3.16). @owner: L. */
public interface ViewHistoryService {
    /** Ghi nhận lượt xem cho user hiện tại (nếu đã đăng nhập). */
    void record(Long productId);
    List<ProductViewHistory> myRecent();
}
