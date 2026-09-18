package com.starshop.service.customer;

import com.starshop.domain.UserFollowProduct;
import java.util.List;

/** Thích sản phẩm — wishlist (F3.15). @owner: L. */
public interface WishlistService {
    List<UserFollowProduct> myWishlist();
    boolean isFollowed(Long productId);
    /** Bật/tắt thích; trả trạng thái mới (true = đang thích). */
    boolean toggle(Long productId);
}
