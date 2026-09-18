package com.starshop.repository;

import com.starshop.domain.Review;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/** @owner: L. Nạp kèm user để JSP hiển thị tên người đánh giá (tránh N+1, open-in-view=false). */
public interface ReviewRepository extends JpaRepository<Review, Long> {
    @EntityGraph(attributePaths = {"user"})
    List<Review> findByProductId(Long productId);
}
