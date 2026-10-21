-- V9 — Đếm lượt yêu thích (wishlist) để sắp xếp "yêu thích nhiều nhất" (F3.x).
-- Theo cùng pattern counter đã có trên products (sold_count, rating_avg): phi chuẩn hoá để sắp xếp nhanh.
ALTER TABLE products ADD COLUMN favorite_count BIGINT NOT NULL DEFAULT 0;

-- Backfill từ dữ liệu follow hiện có.
UPDATE products p
SET favorite_count = (SELECT COUNT(*) FROM user_follow_product f WHERE f.product_id = p.id);
