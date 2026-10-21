package com.starshop.service.customer;

import com.starshop.domain.Order;
import com.starshop.domain.OrderReview;
import com.starshop.domain.enums.CommonEnums.ReviewTarget;
import com.starshop.domain.enums.OrderStatus;
import com.starshop.repository.OrderRepository;
import com.starshop.repository.OrderReviewRepository;
import com.starshop.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

/** @owner: TV1. Đánh giá shop + shipper của đơn đã giao. */
@Service
@RequiredArgsConstructor
public class OrderReviewServiceImpl implements OrderReviewService {

    private final OrderReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final CurrentUser currentUser;

    @Override
    @Transactional(readOnly = true)
    public Map<String, OrderReview> myReviewsFor(Long orderId) {
        Map<String, OrderReview> map = new LinkedHashMap<>();
        for (OrderReview r : reviewRepository.findByOrderIdAndUserId(orderId, currentUser.id())) {
            map.put(r.getTarget().name(), r);
        }
        return map;
    }

    @Override
    @Transactional
    public void submit(Long orderId, ReviewTarget target, int rating, String comment) {
        if (target == null) throw new IllegalArgumentException("Thiếu đối tượng đánh giá");
        if (rating < 1 || rating > 5) throw new IllegalArgumentException("Số sao phải từ 1 đến 5");

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng"));
        Long uid = currentUser.id();
        if (order.getBuyer() == null || !order.getBuyer().getId().equals(uid))
            throw new AccessDeniedException("Không phải đơn của bạn");
        if (order.getStatus() != OrderStatus.DELIVERED)
            throw new IllegalStateException("Chỉ đánh giá khi đơn đã giao thành công");
        if (target == ReviewTarget.SHIPPER && order.getShipper() == null)
            throw new IllegalStateException("Đơn chưa có shipper để đánh giá");

        String cleanComment = (comment == null || comment.isBlank()) ? null : comment.trim();

        OrderReview review = reviewRepository.findByOrderIdAndUserIdAndTarget(orderId, uid, target)
                .orElseGet(() -> OrderReview.builder()
                        .order(order).user(order.getBuyer()).target(target).build());
        review.setRating(rating);
        review.setComment(cleanComment);
        reviewRepository.save(review);
    }
}
