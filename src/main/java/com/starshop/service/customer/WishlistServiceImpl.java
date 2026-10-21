package com.starshop.service.customer;

import com.starshop.domain.UserFollowProduct;
import com.starshop.repository.ProductRepository;
import com.starshop.repository.UserFollowProductRepository;
import com.starshop.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** @owner: L. */
@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final UserFollowProductRepository followRepository;
    private final ProductRepository productRepository;
    private final CurrentUser currentUser;

    @Override
    public List<UserFollowProduct> myWishlist() {
        return followRepository.findByUserId(currentUser.id());
    }

    @Override
    public boolean isFollowed(Long productId) {
        return followRepository.existsByUserIdAndProductId(currentUser.id(), productId);
    }

    @Override
    @Transactional
    public boolean toggle(Long productId) {
        Long uid = currentUser.id();
        var product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại"));
        long fc = product.getFavoriteCount() == null ? 0L : product.getFavoriteCount();

        if (followRepository.existsByUserIdAndProductId(uid, productId)) {
            followRepository.deleteByUserIdAndProductId(uid, productId);
            product.setFavoriteCount(Math.max(0L, fc - 1));   // bỏ thích -> giảm đếm
            productRepository.save(product);
            return false;
        }
        followRepository.save(UserFollowProduct.builder().user(currentUser.get()).product(product).build());
        product.setFavoriteCount(fc + 1);                      // thích -> tăng đếm
        productRepository.save(product);
        return true;
    }
}
