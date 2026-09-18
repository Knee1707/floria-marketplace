package com.starshop.service.customer;

import com.starshop.domain.*;
import com.starshop.repository.*;
import com.starshop.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** @owner: L. Giỏ hàng lưu DB. */
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final CurrentUser currentUser;

    @Override
    @Transactional
    public Cart getMyCart() {
        Long uid = currentUser.id();
        return cartRepository.findByUserId(uid).orElseGet(() -> {
            Cart c = Cart.builder().user(currentUser.get()).build();
            return cartRepository.save(c);
        });
    }

    @Override
    @Transactional
    public void add(Long productId, int quantity) {
        if (quantity < 1) quantity = 1;
        Cart cart = getMyCart();
        Product p = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại"));
        CartItem existing = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId)).findFirst().orElse(null);
        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + quantity);
            cartItemRepository.save(existing);
        } else {
            cartItemRepository.save(CartItem.builder().cart(cart).product(p).quantity(quantity).build());
        }
    }

    @Override
    @Transactional
    public void updateQuantity(Long cartItemId, int quantity) {
        CartItem item = getOwnedItem(cartItemId);
        if (quantity < 1) { cartItemRepository.delete(item); return; }
        item.setQuantity(quantity);
        cartItemRepository.save(item);
    }

    @Override
    @Transactional
    public void remove(Long cartItemId) {
        cartItemRepository.delete(getOwnedItem(cartItemId));
    }

    @Override
    @Transactional
    public void clear() {
        Cart cart = getMyCart();
        cartItemRepository.deleteAll(cart.getItems());
    }

    /** Chống IDOR: item phải thuộc giỏ của user hiện tại. */
    private CartItem getOwnedItem(Long cartItemId) {
        Cart cart = getMyCart();
        return cart.getItems().stream().filter(i -> i.getId().equals(cartItemId)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy mục trong giỏ của bạn"));
    }
}
