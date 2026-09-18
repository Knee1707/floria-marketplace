# LUẬT DB 02 — Schema Anti-Bloat (chống đẻ/phình bảng) · 3NF

> Trigger: "bảng mới", "cột mới", "tách bảng", "3NF", "đẻ bảng", "phình bảng".

## Cổng chống phình DB (trước khi thêm bảng/cột)
- [ ] Dữ liệu này đã có bảng/cột chứa được chưa? → có: dùng lại.
- [ ] Thuộc tính **lặp nhiều dòng** (địa chỉ, ảnh, thẻ)? → **tách bảng con + FK**, KHÔNG nhồi nhiều cột vào bảng cha.
- [ ] Vi phạm **3NF** (phụ thuộc bắc cầu)? → tách bảng.
- [ ] Là **giá trị tính được** (doanh thu, tổng)? → tính khi query/view thay vì lưu (trừ khi có trigger cập nhật).

## Ví dụ (áp cho StarShop)
- User có **nhiều địa chỉ** → `user_address(user_id, ...)`, KHÔNG thêm `address1, ward, district…` vào `users`.
- Nhiều ảnh SP → `product_image`; media đánh giá → `review_media`.
- Thuộc tính SP → `style` + `style_value`. Nhiều–nhiều → bảng nối (`store_managers`, `order_vouchers`).
- Không dùng cột CSV nhồi danh sách; dùng bảng con hoặc `text[]`/`jsonb` đúng chỗ.

## Ranh giới 26 bảng (không thêm ngoài khung, phải qua Leader)
- Cụm khách (L): users, user_levels, otp_tokens, carts, cart_items, orders, order_items, order_vouchers, reviews, review_media, product_view_history, user_follow_*
- Cụm bán/giao (M1): stores, products, promotions, vouchers, transactions, delivery_assignments, notifications, store_managers
- Cụm cấu hình (M2): categories, styles, style_values, commissions, deliveries, store_levels

## ✅ Kiểm
- [ ] Bảng mới thật sự cần (không có chỗ chứa)?
- [ ] Đúng 3NF, không nhồi cột lặp?
- [ ] Thuộc đúng 1 cụm, đã cập nhật ERD?
