package com.starshop.service.customer;

import com.starshop.domain.Product;
import com.starshop.domain.Review;
import com.starshop.domain.ReviewMedia;
import com.starshop.repository.*;
import com.starshop.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/** @owner: L. */
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private static final int MIN_COMMENT = 50;

    private final ReviewRepository reviewRepository;
    private final ReviewMediaRepository reviewMediaRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final CurrentUser currentUser;

    @Override
    public List<Review> forProduct(Long productId) {
        return reviewRepository.findByProductId(productId);
    }

    @Override
    public boolean canReview(Long productId) {
        return orderItemRepository.existsByOrderBuyerIdAndProductId(currentUser.id(), productId);
    }

    @Override
    @Transactional
    public void addReview(Long productId, int rating, String comment, List<String> mediaUrls) {
        if (!canReview(productId))
            throw new AccessDeniedException("Chỉ đánh giá được sản phẩm bạn đã mua");
        if (rating < 1 || rating > 5)
            throw new IllegalArgumentException("Số sao phải từ 1 đến 5");
        if (comment == null || comment.trim().length() < MIN_COMMENT)
            throw new IllegalArgumentException("Bình luận tối thiểu " + MIN_COMMENT + " ký tự");

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại"));

        Review review = reviewRepository.save(Review.builder()
                .product(product).user(currentUser.get()).rating(rating).comment(comment.trim()).build());

        if (mediaUrls != null) {
            for (String url : mediaUrls) {
                if (url == null || url.isBlank()) continue;
                boolean video = url.matches("(?i).*\\.(mp4|webm|mov)$");
                reviewMediaRepository.save(ReviewMedia.builder()
                        .review(review).mediaUrl(url.trim()).mediaType(video ? "video" : "image").build());
            }
        }

        // Cập nhật điểm trung bình của sản phẩm (metric SSOT - rules/database/05)
        recomputeRating(product);
    }

    private void recomputeRating(Product product) {
        List<Review> all = reviewRepository.findByProductId(product.getId());
        if (all.isEmpty()) return;
        double avg = all.stream().mapToInt(Review::getRating).average().orElse(0);
        product.setRatingAvg(BigDecimal.valueOf(avg).setScale(2, RoundingMode.HALF_UP));
        productRepository.save(product);
    }
}
