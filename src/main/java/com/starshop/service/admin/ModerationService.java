package com.starshop.service.admin;

import com.starshop.domain.Product;
import com.starshop.domain.Store;
import com.starshop.domain.User;
import com.starshop.domain.enums.CommonEnums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * F6.4 Kiểm duyệt shop & sản phẩm — trong PHẠM VI người gọi (Admin: toàn hệ thống; Manager: shop được giao).
 * Duyệt/cấm shop dùng hợp đồng có sẵn UserAdminService.setStoreApproved.
 * Kèm phân công Manager ↔ shop (bảng store_managers) — chỉ Admin.
 * @owner: TV3 | @leaf: F6.4
 */
public interface ModerationService {
    List<Store> stores();
    /** storeId = null → mọi shop trong phạm vi; có storeId → "sản phẩm của từng shop" (kiểm phạm vi). */
    Page<Product> products(String keyword, Long storeId, Pageable pageable);
    /** Chỉ cho phép ACTIVE (mở lại) hoặc BANNED (cấm). HIDDEN là quyền của Vendor. */
    void setProductStatus(Long productId, ProductStatus status);

    // ----- Phân công Manager (Admin) -----
    /** storeId → danh sách Manager phụ trách, cho các shop trong phạm vi. */
    Map<Long, List<User>> managersByStore(List<Store> stores);
    /** Tài khoản có vai trò MANAGER để Admin chọn khi phân công. */
    List<User> managerCandidates();
    void assignManager(Long storeId, Long managerId);
    void unassignManager(Long storeId, Long managerId);
}
