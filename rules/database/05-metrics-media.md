# LUẬT DB 05 — Metrics (định nghĩa 1 lần) · Media

> Trigger: "doanh thu", "sold_count", "rating", "metric", "ảnh", "video", "upload", "cloudinary".

## 1. Metrics — định nghĩa 1 lần (SSOT), tránh mỗi nơi tính một kiểu
- `sold_count` = tổng số lượng đã bán (cập nhật khi đơn `DELIVERED`).
- `rating_avg` = trung bình `reviews.rating` (chỉ review hợp lệ).
- `order.total` = `subtotal + shipping_fee − discount`.
- `order.unit_price` = giá SP tại thời điểm mua (chốt, không đổi sau).
- `revenue` (shop) = tổng `order.total` đơn `DELIVERED` − hoa hồng app (`commission`).
- **LUẬT:** metric tính bằng query/service thống nhất; nếu lưu cột thì phải có trigger/service cập nhật, không để lệch.

## 2. Media (ảnh/video) — chống phình DB
- **Ảnh** (SP, ảnh bìa, media đánh giá): upload **Cloudinary**, DB chỉ lưu **URL** (`thumbnail_url`, `media_url`). KHÔNG lưu BLOB.
- **Video**: không host trong DB. Dẫn **link YouTube/Cloudinary** (lưu URL). StarShop bán hoa → video hiếm, ưu tiên link.
- DB chỉ giữ đường dẫn + metadata (loại, kích thước). Storage tách khỏi DB.

## ✅ Kiểm
- [ ] Metric dùng đúng định nghĩa ở đây, không tự tính kiểu khác?
- [ ] Ảnh/video lưu URL (Cloudinary/link), không BLOB trong DB?
