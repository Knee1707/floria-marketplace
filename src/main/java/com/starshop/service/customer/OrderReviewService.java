package com.starshop.service.customer;

import com.starshop.domain.OrderReview;
import com.starshop.domain.enums.CommonEnums.ReviewTarget;

import java.util.Map;

/** Đánh giá cửa hàng & shipper sau khi đơn giao thành công (F3.18). @owner: TV1 */
public interface OrderReviewService {

    /** Đánh giá của tôi cho đơn này, map theo tên đối tượng ("SHOP"/"SHIPPER") để JSP tiền điền form. */
    Map<String, OrderReview> myReviewsFor(Long orderId);

    /**
     * Gửi/ cập nhật đánh giá cho 1 đối tượng của đơn. Ràng buộc: đơn thuộc về mình,
     * trạng thái DELIVERED, rating 1..5; đánh giá shipper yêu cầu đơn đã có shipper.
     * Mỗi đơn chỉ 1 đánh giá / đối tượng (gửi lại = cập nhật).
     */
    void submit(Long orderId, ReviewTarget target, int rating, String comment);
}
