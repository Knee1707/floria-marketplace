package com.starshop.service.customer;

import com.starshop.domain.Cart;

/** Hợp đồng giỏ hàng (F3.8). @owner: L. */
public interface CartService {
    Cart getMyCart();
    void add(Long productId, int quantity);
    void updateQuantity(Long cartItemId, int quantity);
    void remove(Long cartItemId);
    void clear();
}
