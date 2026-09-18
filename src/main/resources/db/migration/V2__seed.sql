-- ============================================================
-- Seed dữ liệu demo. Mật khẩu tất cả tài khoản: 123456
-- (hash BCrypt cost 10). Mỗi thành viên bổ sung seed domain mình ở V{n} mới.
-- ============================================================

INSERT INTO user_levels (name, discount_percent, threshold, created_at, updated_at) VALUES
 ('Đồng', 0, 0, now(), now()),
 ('Bạc', 3, 1000000, now(), now()),
 ('Vàng', 5, 5000000, now(), now());

INSERT INTO store_levels (name, commission_rate, created_at, updated_at) VALUES
 ('Cơ bản', 10, now(), now()),
 ('Đối tác', 7, now(), now());

-- Tài khoản mỗi role (password = 123456)
INSERT INTO users (email, phone, password, full_name, role, enabled, locked, user_level_id, created_at, updated_at) VALUES
 ('user@starshop.vn',    '0900000001', '$2b$10$Nwom1y21804n1CZwhV14PuuXKigxCCUMUryB4WHvyNdrG69eY4IPG', 'Nguyễn Văn User',    'USER',    TRUE, FALSE, 1, now(), now()),
 ('vendor@starshop.vn',  '0900000002', '$2b$10$Nwom1y21804n1CZwhV14PuuXKigxCCUMUryB4WHvyNdrG69eY4IPG', 'Trần Thị Vendor',    'VENDOR',  TRUE, FALSE, 1, now(), now()),
 ('manager@starshop.vn', '0900000003', '$2b$10$Nwom1y21804n1CZwhV14PuuXKigxCCUMUryB4WHvyNdrG69eY4IPG', 'Lê Văn Manager',     'MANAGER', TRUE, FALSE, 1, now(), now()),
 ('admin@starshop.vn',   '0900000004', '$2b$10$Nwom1y21804n1CZwhV14PuuXKigxCCUMUryB4WHvyNdrG69eY4IPG', 'Phạm Thị Admin',     'ADMIN',   TRUE, FALSE, 1, now(), now()),
 ('shipper@starshop.vn', '0900000005', '$2b$10$Nwom1y21804n1CZwhV14PuuXKigxCCUMUryB4WHvyNdrG69eY4IPG', 'Đỗ Văn Shipper',     'SHIPPER', TRUE, FALSE, 1, now(), now());

-- Danh mục hoa
INSERT INTO categories (name, created_at, updated_at) VALUES
 ('Hoa sinh nhật', now(), now()),
 ('Hoa khai trương', now(), now()),
 ('Hoa cưới', now(), now()),
 ('Hoa chia buồn', now(), now());

-- Nhà vận chuyển
INSERT INTO deliveries (name, shipping_fee, created_at, updated_at) VALUES
 ('Giao nhanh nội thành', 20000, now(), now()),
 ('Giao tiêu chuẩn', 30000, now(), now());

-- Cửa hàng của vendor (owner_id = 2)
INSERT INTO stores (name, description, owner_id, store_level_id, approved, created_at, updated_at) VALUES
 ('StarShop Quận 1', 'Cửa hàng hoa tươi trung tâm', 2, 1, TRUE, now(), now()),
 ('StarShop Thủ Đức', 'Chi nhánh Thủ Đức', 2, 1, TRUE, now(), now());

-- Vài sản phẩm mẫu (store_id 1/2, category 1..4)
INSERT INTO products (name, description, price, stock, sold_count, rating_avg, status, store_id, category_id, created_at, updated_at) VALUES
 ('Bó hồng đỏ 20 bông',   'Hồng Ecuador tươi',       350000, 50, 120, 4.8, 'ACTIVE', 1, 1, now(), now()),
 ('Kệ hoa khai trương',    'Kệ 2 tầng rực rỡ',        800000, 20,  45, 4.6, 'ACTIVE', 1, 2, now(), now()),
 ('Hoa cưới cầm tay',      'Tone pastel nhẹ nhàng',   500000, 15,  30, 4.9, 'ACTIVE', 2, 3, now(), now()),
 ('Bó hướng dương',        'Nắng trong tay bạn',      280000, 40,  90, 4.7, 'ACTIVE', 2, 1, now(), now()),
 ('Lẵng hoa chia buồn',    'Trang nghiêm, lịch sự',   650000, 10,  15, 4.5, 'ACTIVE', 1, 4, now(), now());

-- Giỏ hàng cho user
INSERT INTO carts (user_id, created_at, updated_at) VALUES (1, now(), now());

-- Voucher hệ thống demo
INSERT INTO vouchers (code, type, value, min_order, max_discount, quota, used_count, active, created_at, updated_at) VALUES
 ('WELCOME10', 'PRODUCT_PERCENT', 10, 200000, 50000, 100, 0, TRUE, now(), now()),
 ('FREESHIP',  'SHIPPING', 30000, 0, 30000, 200, 0, TRUE, now(), now());
