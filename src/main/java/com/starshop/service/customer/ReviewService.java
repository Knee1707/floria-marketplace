package com.starshop.service.customer;

import com.starshop.domain.Review;
import java.util.List;

/** Đánh giá + bình luận SP đã mua (F3.17). @owner: L. */
public interface ReviewService {
    List<Review> forProduct(Long productId);
    /** Chỉ user đã mua mới được đánh giá; bình luận tối thiểu 50 ký tự. */
    boolean canReview(Long productId);
    void addReview(Long productId, int rating, String comment, List<String> mediaUrls);
}
