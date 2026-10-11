-- ============================================================
-- V4 (TV2) — Seed dữ liệu Kênh Bán (Vendor - D4), Giao Vận (Shipper - D5) & Thông báo (D7).
-- Gồm: Cập nhật ảnh cho 5 SP gốc, thêm shop thứ 3, ~30 sản phẩm hoa tươi,
-- voucher/promotion của shop, đơn hàng mẫu 7 trạng thái, phân công Shipper, giao dịch ví & thông báo.
-- Tham chiếu theo khoá tự nhiên (email / tên) để tương thích hoàn toàn với V2, V3 và V5.
-- @owner: TV2
-- ============================================================

-- 1. Cập nhật ảnh đại diện cho 2 cửa hàng gốc và 5 sản phẩm gốc ở V2
UPDATE stores
SET avatar_url = 'https://images.unsplash.com/photo-1561181286-d3fee7d55364?auto=format&fit=crop&w=300&q=80',
    cover_url  = 'https://images.unsplash.com/photo-1490750967868-88aa4486c946?auto=format&fit=crop&w=1200&q=80'
WHERE name IN ('StarShop Quận 1', 'StarShop Thủ Đức') AND avatar_url IS NULL;

UPDATE products SET thumbnail_url = 'https://images.unsplash.com/photo-1518709268805-4e9042af9f23?auto=format&fit=crop&w=600&q=80' WHERE name = 'Bó hồng đỏ 20 bông' AND thumbnail_url IS NULL;
UPDATE products SET thumbnail_url = 'https://images.unsplash.com/photo-1526047932273-341f2a7631f9?auto=format&fit=crop&w=600&q=80' WHERE name = 'Kệ hoa khai trương' AND thumbnail_url IS NULL;
UPDATE products SET thumbnail_url = 'https://images.unsplash.com/photo-1508615039623-a25605d2b022?auto=format&fit=crop&w=600&q=80' WHERE name = 'Hoa cưới cầm tay' AND thumbnail_url IS NULL;
UPDATE products SET thumbnail_url = 'https://images.unsplash.com/photo-1470509037663-253afd7f0f51?auto=format&fit=crop&w=600&q=80' WHERE name = 'Bó hướng dương' AND thumbnail_url IS NULL;
UPDATE products SET thumbnail_url = 'https://images.unsplash.com/photo-1468327768560-75b778cbb551?auto=format&fit=crop&w=600&q=80' WHERE name = 'Lẵng hoa chia buồn' AND thumbnail_url IS NULL;

-- 2. Thêm cửa hàng thứ 3 cho vendor@starshop.vn (đủ 3 shop theo HUONG_DAN_03)
INSERT INTO stores (name, description, avatar_url, cover_url, owner_id, store_level_id, approved, created_at, updated_at)
SELECT 'StarShop Phú Nhuận',
       'Boutique hoa nghệ thuật nhập khẩu Hà Lan & Ecuador',
       'https://images.unsplash.com/photo-1508615039623-a25605d2b022?auto=format&fit=crop&w=300&q=80',
       'https://images.unsplash.com/photo-1526047932273-341f2a7631f9?auto=format&fit=crop&w=1200&q=80',
       u.id,
       (SELECT id FROM store_levels WHERE name = 'Đối tác' LIMIT 1),
       TRUE,
       now(),
       now()
FROM users u
WHERE u.email = 'vendor@starshop.vn'
  AND NOT EXISTS (SELECT 1 FROM stores WHERE name = 'StarShop Phú Nhuận');

-- 3. Thêm 26 sản phẩm hoa tươi (cộng 5 SP ở V2 = 31 sản phẩm đa dạng danh mục và cửa hàng)
INSERT INTO products (name, description, price, stock, thumbnail_url, sold_count, rating_avg, status, store_id, category_id, created_at, updated_at)
SELECT v.name, v.description, v.price, v.stock, v.thumb, v.sold, v.rating, 'ACTIVE', s.id, c.id, now(), now()
FROM (VALUES
    ('Bó Hồng Pastel Ohara 15 Bông',       'Hồng Ohara thơm dịu phối lá bạc nhập khẩu',          420000, 35, 'https://images.unsplash.com/photo-1561181286-d3fee7d55364?auto=format&fit=crop&w=600&q=80',  85, 4.9, 'StarShop Quận 1',    'Hoa sinh nhật'),
    ('Giỏ Hoa Cẩm Tú Cầu Xanh',            'Cẩm tú cầu Đà Lạt phối cúc mẫu đơn thanh lịch',       550000, 25, 'https://images.unsplash.com/photo-1526047932273-341f2a7631f9?auto=format&fit=crop&w=600&q=80',  64, 4.8, 'StarShop Quận 1',    'Hoa sinh nhật'),
    ('Bó Hoa Tulip Trắng Hà Lan',          '10 cành Tulip trắng tinh khôi nhập khẩu',            750000, 18, 'https://images.unsplash.com/photo-1508615039623-a25605d2b022?auto=format&fit=crop&w=600&q=80',  52, 4.9, 'StarShop Quận 1',    'Hoa sinh nhật'),
    ('Hộp Hoa Hồng Đỏ Trái Tim',           'Hộp mica sang trọng 30 bông hồng đỏ',                680000, 20, 'https://images.unsplash.com/photo-1518709268805-4e9042af9f23?auto=format&fit=crop&w=600&q=80', 110, 4.9, 'StarShop Quận 1',    'Hoa sinh nhật'),
    ('Bó Hoa Baby Trắng Khổng Lồ',         'Baby Hà Lan trắng tinh khiết bó tròn lớn',           490000, 30, 'https://images.unsplash.com/photo-1490750967868-88aa4486c946?auto=format&fit=crop&w=600&q=80',  95, 4.7, 'StarShop Thủ Đức',   'Hoa sinh nhật'),
    ('Giỏ Hoa Hướng Dương Nắng Mới',       '12 bông hướng dương kết hợp hồng vàng rực rỡ',       460000, 28, 'https://images.unsplash.com/photo-1470509037663-253afd7f0f51?auto=format&fit=crop&w=600&q=80',  73, 4.8, 'StarShop Thủ Đức',   'Hoa sinh nhật'),
    ('Bó Hoa Mẫu Đơn Hồng Phấn',           'Mẫu đơn Peony nở rộ sang trọng tặng sinh nhật',      950000, 12, 'https://images.unsplash.com/photo-1563241527-3004b7be0ffd?auto=format&fit=crop&w=600&q=80',  40, 5.0, 'StarShop Phú Nhuận', 'Hoa sinh nhật'),
    ('Kệ Hoa Khai Trương Hồng Phát',       'Kệ 2 tầng đồng tiền đỏ, hồng môn và lan vũ nữ',     1250000, 15, 'https://images.unsplash.com/photo-1526047932273-341f2a7631f9?auto=format&fit=crop&w=600&q=80',  38, 4.8, 'StarShop Quận 1',    'Hoa khai trương'),
    ('Lẵng Hoa Khai Trương Thịnh Vượng',   'Lẵng hoa để bàn tone vàng cam may mắn',              690000, 22, 'https://images.unsplash.com/photo-1470509037663-253afd7f0f51?auto=format&fit=crop&w=600&q=80',  58, 4.7, 'StarShop Quận 1',    'Hoa khai trương'),
    ('Chậu Lan Hồ Điệp Vàng 5 Cành',      'Lan Hồ Điệp loại A chậu sứ trắng cao cấp',          1850000, 10, 'https://images.unsplash.com/photo-1567696153798-9111f9cd3d0d?auto=format&fit=crop&w=600&q=80',  29, 4.9, 'StarShop Quận 1',    'Hoa khai trương'),
    ('Kệ Hoa Khai Trương Đại Cát',         'Thiết kế hiện đại với hồng Juliet và lan Mokara',   1450000, 12, 'https://images.unsplash.com/photo-1561181286-d3fee7d55364?auto=format&fit=crop&w=600&q=80',  31, 4.8, 'StarShop Thủ Đức',   'Hoa khai trương'),
    ('Giỏ Hoa Khai Trương Phú Quý',        'Giỏ gỗ cắm hoa hướng dương, hồng cam Spirit',        780000, 20, 'https://images.unsplash.com/photo-1490750967868-88aa4486c946?auto=format&fit=crop&w=600&q=80',  47, 4.7, 'StarShop Thủ Đức',   'Hoa khai trương'),
    ('Chậu Lan Hồ Điệp Trắng 9 Cành',     'Quà tặng đối tác khai trương đẳng cấp',              2900000,  8, 'https://images.unsplash.com/photo-1508615039623-a25605d2b022?auto=format&fit=crop&w=600&q=80',  19, 5.0, 'StarShop Phú Nhuận', 'Hoa khai trương'),
    ('Hoa Cưới Cầm Tay Hồng Juliet',       'Bó hoa cưới cô dâu hồng Juliet phối thảo đường',      850000, 15, 'https://images.unsplash.com/photo-1523438885200-e635ba2c371e?auto=format&fit=crop&w=600&q=80',  34, 4.9, 'StarShop Quận 1',    'Hoa cưới'),
    ('Hoa Cưới Cầm Tay Rum Trắng',         'Hoa Calla Lily trắng thanh lịch kèm hoa cài áo',      720000, 16, 'https://images.unsplash.com/photo-1508615039623-a25605d2b022?auto=format&fit=crop&w=600&q=80',  41, 4.8, 'StarShop Quận 1',    'Hoa cưới'),
    ('Hoa Cưới Linh Lan Tinh Khôi',        'Hoa Linh Lan kết hợp hồng trắng nhập khẩu',          1350000,  8, 'https://images.unsplash.com/photo-1468327768560-75b778cbb551?auto=format&fit=crop&w=600&q=80',  22, 5.0, 'StarShop Thủ Đức',   'Hoa cưới'),
    ('Bộ Hoa Trang Trí Bàn Gallery Cưới',  'Combo 3 bình hoa tone trắng hồng kem lãng mạn',      1600000, 10, 'https://images.unsplash.com/photo-1561181286-d3fee7d55364?auto=format&fit=crop&w=600&q=80',  17, 4.8, 'StarShop Phú Nhuận', 'Hoa cưới'),
    ('Hoa Cài Áo Chú Rể & Phù Rể (Set 6)', 'Set 6 hoa cài áo đồng bộ với hoa cầm tay cô dâu',     360000, 40, 'https://images.unsplash.com/photo-1518709268805-4e9042af9f23?auto=format&fit=crop&w=600&q=80',  55, 4.7, 'StarShop Phú Nhuận', 'Hoa cưới'),
    ('Vòng Hoa Chia Buồn Thành Kính',      'Vòng hoa lan trắng và cúc trắng trang trọng',         950000, 15, 'https://images.unsplash.com/photo-1468327768560-75b778cbb551?auto=format&fit=crop&w=600&q=80',  28, 4.8, 'StarShop Quận 1',    'Hoa chia buồn'),
    ('Kệ Hoa Viếng Trắng Tím Trang Nghiêm','Kệ hoa 2 tầng lan Thái trắng tím phân ưu',           1150000, 12, 'https://images.unsplash.com/photo-1508615039623-a25605d2b022?auto=format&fit=crop&w=600&q=80',  24, 4.7, 'StarShop Quận 1',    'Hoa chia buồn'),
    ('Giỏ Hoa Trắng Tưởng Niệm',           'Giỏ hoa hồng trắng và ly trắng thanh khiết',          580000, 20, 'https://images.unsplash.com/photo-1490750967868-88aa4486c946?auto=format&fit=crop&w=600&q=80',  33, 4.8, 'StarShop Thủ Đức',   'Hoa chia buồn'),
    ('Kệ Hoa Chia Buồn Lan Hồ Điệp Trắng', 'Kệ hoa cao cấp viếng tang lễ trang trọng',           1650000, 10, 'https://images.unsplash.com/photo-1567696153798-9111f9cd3d0d?auto=format&fit=crop&w=600&q=80',  14, 4.9, 'StarShop Phú Nhuận', 'Hoa chia buồn'),
    ('Bó Hoa Cúc Mẫu Đơn Cam Đào',        'Cúc mẫu đơn nhập khẩu phối lá khuynh diệp',          520000, 30, 'https://images.unsplash.com/photo-1563241527-3004b7be0ffd?auto=format&fit=crop&w=600&q=80',  67, 4.8, 'StarShop Quận 1',    'Hoa sinh nhật'),
    ('Bó Hoa Cẩm Chướng Hồng Tặng Mẹ',     '20 cành cẩm chướng hồng thể hiện lòng biết ơn',       390000, 45, 'https://images.unsplash.com/photo-1561181286-d3fee7d55364?auto=format&fit=crop&w=600&q=80',  88, 4.9, 'StarShop Quận 1',    'Hoa sinh nhật'),
    ('Bình Hoa Thủy Tinh Hồng Capuchino',  'Bình hoa cắm sẵn tone nâu tây hiện đại',              640000, 18, 'https://images.unsplash.com/photo-1526047932273-341f2a7631f9?auto=format&fit=crop&w=600&q=80',  49, 4.8, 'StarShop Thủ Đức',   'Hoa sinh nhật'),
    ('Bó Hoa Lavender Khô Thơm Lâu',       'Oải hương Pháp sấy khô giữ hương 12 tháng',           410000, 50, 'https://images.unsplash.com/photo-1468327768560-75b778cbb551?auto=format&fit=crop&w=600&q=80',  76, 4.7, 'StarShop Phú Nhuận', 'Hoa sinh nhật')
) AS v(name, description, price, stock, thumb, sold, rating, store_name, cat_name)
JOIN stores s ON s.name = v.store_name
JOIN categories c ON c.name = v.cat_name AND c.parent_id IS NULL;

-- 4. Thêm Voucher & Chương trình khuyến mãi riêng cho các Shop của Vendor (F4.5)
INSERT INTO vouchers (code, type, value, min_order, max_discount, quota, used_count, start_at, end_at, store_id, active, created_at, updated_at)
SELECT v.code, v.type, v.val, v.min_ord, v.max_disc, v.quota, v.used, now() - interval '5 days', now() + interval '30 days', s.id, TRUE, now(), now()
FROM (VALUES
    ('Q1GIAM15',   'PRODUCT_PERCENT', 15,    300000, 75000, 100, 12, 'StarShop Quận 1'),
    ('Q1FREESHIP', 'SHIPPING',        20000, 250000, 20000, 150, 25, 'StarShop Quận 1'),
    ('TDGIAM10',   'PRODUCT_PERCENT', 10,    200000, 50000,  80,  8, 'StarShop Thủ Đức'),
    ('PNVIP20',    'PRODUCT_PERCENT', 20,    500000, 100000, 50,  5, 'StarShop Phú Nhuận')
) AS v(code, type, val, min_ord, max_disc, quota, used, store_name)
JOIN stores s ON s.name = v.store_name
WHERE NOT EXISTS (SELECT 1 FROM vouchers WHERE code = v.code);

INSERT INTO promotions (store_id, type, value, start_at, end_at, quota, created_at, updated_at)
SELECT s.id, v.type, v.val, now() - interval '5 days', now() + interval '30 days', v.quota, now(), now()
FROM (VALUES
    ('StarShop Quận 1',    'PRODUCT_PERCENT', 15,    100),
    ('StarShop Quận 1',    'SHIPPING',        20000, 150),
    ('StarShop Thủ Đức',   'PRODUCT_PERCENT', 10,     80),
    ('StarShop Phú Nhuận', 'PRODUCT_PERCENT', 20,     50)
) AS v(store_name, type, val, quota)
JOIN stores s ON s.name = v.store_name;

-- 5. Thêm 8 đơn hàng mẫu (DEMO-TV2-001..008) ở các trạng thái của StarShop Quận 1 để test F4.4, F4.6, F4.7, F5.1–F5.3
INSERT INTO orders (code, buyer_id, store_id, shipper_id, status, payment_method, paid, subtotal, shipping_fee, discount, total, shipping_address, created_at, updated_at)
SELECT v.code,
       (SELECT id FROM users WHERE email = 'user@starshop.vn'),
       (SELECT id FROM stores WHERE name = 'StarShop Quận 1'),
       CASE WHEN v.with_shipper THEN (SELECT id FROM users WHERE email = 'shipper@starshop.vn') ELSE NULL END,
       v.status,
       v.pay_method,
       v.is_paid,
       v.subtotal,
       20000,
       v.discount,
       v.subtotal + 20000 - v.discount,
       v.addr,
       now() - make_interval(days => v.days_ago),
       now() - make_interval(days => v.days_ago)
FROM (VALUES
    ('DEMO-TV2-001', 'NEW',       'COD',   FALSE, FALSE,  700000,     0, '01 Võ Văn Ngân, Phường Linh Chiểu, TP. Thủ Đức', 0),
    ('DEMO-TV2-002', 'CONFIRMED', 'VNPAY', TRUE,  FALSE,  840000, 40000, '45 Nguyễn Thị Minh Khai, Quận 1, TP.HCM',       1),
    ('DEMO-TV2-003', 'PICKED_UP', 'COD',   FALSE, TRUE,   550000,     0, '120 Lê Lợi, Phường Bến Thành, Quận 1, TP.HCM',  1),
    ('DEMO-TV2-004', 'SHIPPING',  'VNPAY', TRUE,  TRUE,   750000, 50000, '88 Đồng Khởi, Quận 1, TP.HCM',                  2),
    ('DEMO-TV2-005', 'DELIVERED', 'COD',   TRUE,  TRUE,  1250000,     0, '210 Điện Biên Phủ, Quận 3, TP.HCM',             2),
    ('DEMO-TV2-006', 'DELIVERED', 'VNPAY', TRUE,  TRUE,   680000, 30000, '15 Pasteur, Quận 1, TP.HCM',                    3),
    ('DEMO-TV2-007', 'DELIVERED', 'COD',   TRUE,  TRUE,   850000,     0, '300 Hai Bà Trưng, Quận 1, TP.HCM',              5),
    ('DEMO-TV2-008', 'RETURNED',  'COD',   FALSE, TRUE,   350000,     0, '99 Cách Mạng Tháng 8, Quận 3, TP.HCM',          4)
) AS v(code, status, pay_method, is_paid, with_shipper, subtotal, discount, addr, days_ago)
WHERE NOT EXISTS (SELECT 1 FROM orders WHERE code = v.code);

-- Dòng chi tiết đơn hàng tương ứng cho DEMO-TV2-001..008
INSERT INTO order_items (order_id, product_id, quantity, unit_price, created_at, updated_at)
SELECT o.id,
       (SELECT id FROM products WHERE name = v.prod_name LIMIT 1),
       v.qty,
       v.unit_price,
       o.created_at,
       o.updated_at
FROM (VALUES
    ('DEMO-TV2-001', 'Bó hồng đỏ 20 bông',             2,  350000),
    ('DEMO-TV2-002', 'Bó Hồng Pastel Ohara 15 Bông',   2,  420000),
    ('DEMO-TV2-003', 'Giỏ Hoa Cẩm Tú Cầu Xanh',        1,  550000),
    ('DEMO-TV2-004', 'Bó Hoa Tulip Trắng Hà Lan',      1,  750000),
    ('DEMO-TV2-005', 'Kệ Hoa Khai Trương Hồng Phát',   1, 1250000),
    ('DEMO-TV2-006', 'Hộp Hoa Hồng Đỏ Trái Tim',       1,  680000),
    ('DEMO-TV2-007', 'Hoa Cưới Cầm Tay Hồng Juliet',   1,  850000),
    ('DEMO-TV2-008', 'Bó hồng đỏ 20 bông',             1,  350000)
) AS v(order_code, prod_name, qty, unit_price)
JOIN orders o ON o.code = v.order_code;

-- 6. Phân công giao hàng cho Shipper (shipper@starshop.vn) ứng với các đơn PICKED_UP, SHIPPING, DELIVERED, RETURNED (F5.1–F5.3)
INSERT INTO delivery_assignments (order_id, shipper_id, status, assigned_at, delivered_at, created_at, updated_at)
SELECT o.id,
       (SELECT id FROM users WHERE email = 'shipper@starshop.vn'),
       v.deliv_status,
       o.created_at,
       CASE WHEN v.deliv_status = 'DELIVERED' THEN o.updated_at + interval '2 hours' ELSE NULL END,
       o.created_at,
       o.updated_at
FROM (VALUES
    ('DEMO-TV2-003', 'ASSIGNED'),
    ('DEMO-TV2-004', 'DELIVERING'),
    ('DEMO-TV2-005', 'DELIVERED'),
    ('DEMO-TV2-006', 'DELIVERED'),
    ('DEMO-TV2-007', 'DELIVERED'),
    ('DEMO-TV2-008', 'FAILED')
) AS v(order_code, deliv_status)
JOIN orders o ON o.code = v.order_code;

-- 7. Giao dịch ví cửa hàng mẫu (SALE, COMMISSION, WITHDRAW) cho StarShop Quận 1 (F4.6)
INSERT INTO transactions (store_id, order_id, type, amount, note, created_at, updated_at)
SELECT (SELECT id FROM stores WHERE name = 'StarShop Quận 1'),
       o.id,
       v.tx_type,
       v.amount,
       v.note,
       o.updated_at,
       o.updated_at
FROM (VALUES
    ('DEMO-TV2-005', 'SALE',       1270000, 'Doanh thu đơn hàng #DEMO-TV2-005'),
    ('DEMO-TV2-005', 'COMMISSION',  127000, 'Chiết khấu sàn (10%) đơn #DEMO-TV2-005'),
    ('DEMO-TV2-006', 'SALE',        670000, 'Doanh thu đơn hàng #DEMO-TV2-006'),
    ('DEMO-TV2-006', 'COMMISSION',   67000, 'Chiết khấu sàn (10%) đơn #DEMO-TV2-006'),
    ('DEMO-TV2-007', 'SALE',        870000, 'Doanh thu đơn hàng #DEMO-TV2-007'),
    ('DEMO-TV2-007', 'COMMISSION',   87000, 'Chiết khấu sàn (10%) đơn #DEMO-TV2-007')
) AS v(order_code, tx_type, amount, note)
JOIN orders o ON o.code = v.order_code;

INSERT INTO transactions (store_id, order_id, type, amount, note, created_at, updated_at)
VALUES (
    (SELECT id FROM stores WHERE name = 'StarShop Quận 1'),
    NULL,
    'WITHDRAW',
    500000,
    'Rút tiền: Vietcombank - 0123456789 - TRAN THI VENDOR',
    now() - interval '1 day',
    now() - interval '1 day'
);

-- 8. Thông báo mẫu cho Vendor và Shipper (F7.1)
INSERT INTO notifications (user_id, type, content, is_read, created_at, updated_at)
VALUES
    ((SELECT id FROM users WHERE email = 'vendor@starshop.vn'),  'NEW_ORDER',    'Bạn có đơn hàng mới #DEMO-TV2-001 đang chờ xác nhận.', FALSE, now() - interval '2 hours', now() - interval '2 hours'),
    ((SELECT id FROM users WHERE email = 'vendor@starshop.vn'),  'ORDER_STATUS', 'Shipper Đỗ Văn Shipper đã giao thành công đơn #DEMO-TV2-005.', TRUE,  now() - interval '1 day',   now() - interval '1 day'),
    ((SELECT id FROM users WHERE email = 'shipper@starshop.vn'), 'ASSIGNMENT',   'Bạn được phân công giao đơn #DEMO-TV2-003 từ StarShop Quận 1.', FALSE, now() - interval '1 hour',  now() - interval '1 hour'),
    ((SELECT id FROM users WHERE email = 'shipper@starshop.vn'), 'ASSIGNMENT',   'Đơn #DEMO-TV2-004 đang trong lộ trình giao hàng của bạn.', FALSE, now() - interval '3 hours', now() - interval '3 hours');
