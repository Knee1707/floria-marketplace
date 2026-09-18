package com.starshop.domain;

import jakarta.persistence.*;
import lombok.*;

/** Ảnh/video của đánh giá — F3.17. @owner: L (bảng), TV2 (upload Cloudinary). Bảng review_media (V1). */
@Entity @Table(name = "review_media")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ReviewMedia extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @Column(name = "media_url", nullable = false, length = 500)
    private String mediaUrl;

    @Column(name = "media_type", nullable = false, length = 20)
    private String mediaType; // image | video
}
