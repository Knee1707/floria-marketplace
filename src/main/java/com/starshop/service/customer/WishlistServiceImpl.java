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
        if (followRepository.existsByUserIdAndProductId(uid, productId)) {
            followRepository.deleteByUserIdAndProductId(uid, productId);
            return false;
        }
        var product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại"));
        followRepository.save(UserFollowProduct.builder().user(currentUser.get()).product(product).build());
        return true;
    }
}
