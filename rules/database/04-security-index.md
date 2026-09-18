# LUẬT DB 04 — Bảo mật · Index · Ràng buộc

> Trigger: "bảo mật", "quyền", "RLS", "Spring Security", "index", "khóa ngoại", "ràng buộc".

## 1. Bảo mật (StarShop KHÔNG có RLS như Supabase — bảo vệ ở tầng ứng dụng)
- **Spring Security**: phân quyền URL theo 6 role (`/admin`, `/vendor`, `/shipper`…).
- **Kiểm quyền sở hữu ở tầng Service**: User chỉ đọc/sửa dữ liệu của mình (chống **IDOR**), Vendor chỉ thao tác SP/đơn của shop mình.
- Mật khẩu **BCrypt**; **JWT** cho REST + handshake WebSocket; **CSRF** cho form JSP.

## 2. Toàn vẹn & khóa
- PK mọi bảng; FK `ON DELETE`: bảng con dịch/ảnh `CASCADE`, FK nghiệp vụ `RESTRICT`.
- UNIQUE chống trùng: `cart_items(cart_id,product_id)`, `user_follow_*(user_id,target)`, `orders.code`, `users.email`.
- CHECK enum: `status`, `order_status`, `payment_method`, `product_status`, `rating BETWEEN 1 AND 5`.

## 3. Index (hiệu năng)
- Mọi **FK** + cột lọc/sắp xếp.
- **Partial index**: `WHERE deleted_at IS NULL` / `WHERE status='ACTIVE'`.
- **GIN** cho cột mảng (nếu dùng `text[]`).

## 4. Soft delete & audit (khuyến nghị)
- `deleted_at` thay xoá cứng; public chỉ thấy `deleted_at IS NULL`.
- `created_at/updated_at/created_by/updated_by`; cân nhắc `activity_log`.

## ✅ Kiểm
- [ ] SecurityConfig phủ đủ 6 role? Service có kiểm sở hữu (IDOR)?
- [ ] FK có index? UNIQUE chống trùng đủ?
- [ ] Mật khẩu BCrypt, không lưu plaintext?
