package com.starshop.service.vendor;

import com.starshop.domain.Store;

/**
 * HỢP ĐỒNG — TV2 viết StoreServiceImpl (@owner: TV2 | F4.1, F4.2).
 * Xem HUONG_DAN_03_TV2. Leader khai báo chữ ký; TV2 điền thân.
 */
public interface StoreService {
    /** F4.1: đăng ký shop cho user hiện tại, nâng role -> VENDOR. */
    Store registerStore(String name, String description);
    /** Shop của vendor hiện tại (giả định 1 vendor 1 shop chính). */
    Store myStore();
    /** F4.2: cập nhật thông tin/ảnh shop. */
    void updateStore(Long storeId, String name, String description, String avatarUrl, String coverUrl);
}
