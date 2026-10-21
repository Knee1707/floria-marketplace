package com.starshop.repository;

import com.starshop.domain.OrderReview;
import com.starshop.domain.enums.CommonEnums.ReviewTarget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/** @owner: TV1 — đánh giá shop/shipper theo đơn. */
public interface OrderReviewRepository extends JpaRepository<OrderReview, Long> {

    List<OrderReview> findByOrderIdAndUserId(Long orderId, Long userId);

    Optional<OrderReview> findByOrderIdAndUserIdAndTarget(Long orderId, Long userId, ReviewTarget target);
}
