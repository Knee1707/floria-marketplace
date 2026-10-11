package com.starshop.service.admin;

import com.starshop.domain.Product;
import com.starshop.domain.Store;
import com.starshop.domain.enums.CommonEnums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * F6.4 Kiểm duyệt shop & sản phẩm — trong PHẠM VI người gọi (Admin: toàn hệ thống; Manager: shop được giao).
 * Duyệt/cấm shop dùng hợp đồng có sẵn UserAdminService.setStoreApproved.
 * @owner: TV3 | @leaf: F6.4
 */
public interface ModerationService {
    List<Store> stores();
    Page<Product> products(String keyword, Pageable pageable);
    /** Chỉ cho phép ACTIVE (mở lại) hoặc BANNED (cấm). HIDDEN là quyền của Vendor. */
    void setProductStatus(Long productId, ProductStatus status);
}
