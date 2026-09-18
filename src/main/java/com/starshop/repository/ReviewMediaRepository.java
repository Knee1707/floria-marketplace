package com.starshop.repository;

import com.starshop.domain.ReviewMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/** @owner: L. */
public interface ReviewMediaRepository extends JpaRepository<ReviewMedia, Long> {
    List<ReviewMedia> findByReviewId(Long reviewId);
}
