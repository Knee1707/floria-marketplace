package com.starshop.service.vendor;

import com.starshop.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

/**
 * HỢP ĐỒNG — TV2 viết VendorProductServiceImpl (@owner: TV2 | F4.3).
 * Nhớ kiểm ownership: chỉ thao tác SP của shop mình (chống IDOR).
 */
public interface VendorProductService {
    Page<Product> myProducts(Pageable pageable);
    Product get(Long productId);
    /** id null = tạo mới. Ảnh: dùng CloudinaryService.upload trước, truyền URL vào đây. */
    void save(Long id, String name, String description, BigDecimal price, Integer stock,
              Long categoryId, String thumbnailUrl);
    void hide(Long productId);
}
