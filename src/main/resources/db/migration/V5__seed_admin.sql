-- ============================================================
-- V5 (TV3) — Seed cấu hình quản trị: danh mục con, thuộc tính SP, chiết khấu,
-- nhà vận chuyển, cấp độ user/shop, phân công Manager + DỮ LIỆU DEMO cho dashboard.
-- Không tạo bảng/cột mới (chỉ INSERT vào bảng có sẵn của V1). Không sửa V1/V2/V3.
-- Tham chiếu theo khoá tự nhiên (email / tên) thay vì id cứng để không lệch khi V4 (TV2) chèn thêm dữ liệu.
-- @owner: TV3
-- ============================================================

-- ---------- F6.2 Danh mục con (danh mục gốc đã có ở V2) ----------
INSERT INTO categories (name, parent_id, created_at, updated_at)
SELECT v.name, p.id, now(), now()
FROM (VALUES
    ('Hoa sinh nhật người yêu',   'Hoa sinh nhật'),
    ('Hoa sinh nhật mẹ',          'Hoa sinh nhật'),
    ('Kệ hoa khai trương 2 tầng', 'Hoa khai trương'),
    ('Lẵng hoa khai trương',      'Hoa khai trương'),
    ('Hoa cầm tay cô dâu',        'Hoa cưới'),
    ('Hoa trang trí tiệc cưới',   'Hoa cưới'),
    ('Vòng hoa chia buồn',        'Hoa chia buồn')
) AS v(name, parent_name)
JOIN categories p ON p.name = v.parent_name AND p.parent_id IS NULL;

INSERT INTO categories (name, created_at, updated_at) VALUES
    ('Hoa tốt nghiệp', now(), now()),
    ('Hoa ngày lễ', now(), now());

INSERT INTO categories (name, parent_id, created_at, updated_at)
SELECT v.name, p.id, now(), now()
FROM (VALUES ('Hoa Valentine 14/2'), ('Hoa 8/3'), ('Hoa 20/10'), ('Hoa 20/11')) AS v(name)
JOIN categories p ON p.name = 'Hoa ngày lễ' AND p.parent_id IS NULL;

-- ---------- F6.3 Thuộc tính sản phẩm (style + style_values) ----------
INSERT INTO styles (name, created_at, updated_at) VALUES
    ('Màu sắc', now(), now()),
    ('Dịp', now(), now()),
    ('Kiểu dáng', now(), now()),
    ('Kích thước', now(), now());

INSERT INTO style_values (style_id, value, created_at, updated_at)
SELECT s.id, v.value, now(), now()
FROM (VALUES
    ('Màu sắc', 'Đỏ'), ('Màu sắc', 'Hồng'), ('Màu sắc', 'Trắng'), ('Màu sắc', 'Vàng'),
    ('Màu sắc', 'Tím'), ('Màu sắc', 'Pastel'), ('Màu sắc', 'Nhiều màu'),
    ('Dịp', 'Sinh nhật'), ('Dịp', 'Khai trương'), ('Dịp', 'Cưới hỏi'), ('Dịp', 'Chia buồn'),
    ('Dịp', 'Tốt nghiệp'), ('Dịp', 'Kỷ niệm'),
    ('Kiểu dáng', 'Bó'), ('Kiểu dáng', 'Giỏ'), ('Kiểu dáng', 'Lẵng'), ('Kiểu dáng', 'Kệ'), ('Kiểu dáng', 'Hộp'),
    ('Kích thước', 'Nhỏ'), ('Kích thước', 'Vừa'), ('Kích thước', 'Lớn')
) AS v(style_name, value)
JOIN styles s ON s.name = v.style_name;

-- ---------- F6.5 Chiết khấu app (store_id NULL = mặc định toàn sàn) ----------
INSERT INTO commissions (store_id, rate, note, created_at, updated_at) VALUES
    (NULL, 10.00, 'Mức mặc định toàn sàn', now(), now());

INSERT INTO commissions (store_id, rate, note, created_at, updated_at)
SELECT s.id, v.rate, v.note, now(), now()
FROM (VALUES
    ('StarShop Quận 1',  8.00, 'Đối tác lâu năm'),
    ('StarShop Thủ Đức', 10.00, 'Theo mức cơ bản')
) AS v(store_name, rate, note)
JOIN stores s ON s.name = v.store_name;

-- ---------- F6.7 Nhà vận chuyển (bổ sung, 2 dịch vụ đầu đã có ở V2) ----------
INSERT INTO deliveries (name, shipping_fee, created_at, updated_at) VALUES
    ('Giao hoả tốc 2 giờ', 50000, now(), now()),
    ('Giao liên tỉnh', 45000, now(), now());

-- ---------- Cấp độ user / shop (bổ sung) ----------
INSERT INTO user_levels (name, discount_percent, threshold, created_at, updated_at) VALUES
    ('Kim cương', 8, 15000000, now(), now());

INSERT INTO store_levels (name, commission_rate, created_at, updated_at) VALUES
    ('Chiến lược', 5, now(), now());

-- ---------- Phân công Manager (store_managers) để demo khu /manager ----------
INSERT INTO store_managers (store_id, manager_id, created_at, updated_at)
SELECT s.id, u.id, now(), now()
FROM stores s JOIN users u ON u.email = 'manager@starshop.vn'
WHERE s.name = 'StarShop Quận 1';

-- ============================================================
-- DỮ LIỆU DEMO cho dashboard (F6.8–F6.10) — chỉ để biểu đồ có số liệu khi chấm/demo.
-- Mã đơn tiền tố DEMO-TV3-; Leader có thể bỏ khối này nếu V4 đã có đơn mẫu.
-- ============================================================

-- 12 khách hàng demo đăng ký rải rác trong 30 ngày (mật khẩu 123456) — để có biểu đồ "user mới" + phân trang.
INSERT INTO users (email, phone, password, full_name, role, enabled, locked, user_level_id, created_at, updated_at)
SELECT 'khach' || lpad(g::text, 2, '0') || '@starshop.vn',
       '09110000' || lpad(g::text, 2, '0'),
       '$2b$10$Nwom1y21804n1CZwhV14PuuXKigxCCUMUryB4WHvyNdrG69eY4IPG',
       'Khách demo ' || lpad(g::text, 2, '0'),
       'USER',
       g <> 12,          -- khach12: chưa kích hoạt OTP
       g = 11,           -- khach11: bị khoá
       (SELECT id FROM user_levels WHERE name = 'Đồng' LIMIT 1),
       now() - make_interval(days => g * 2 + 1),
       now() - make_interval(days => g * 2 + 1)
FROM generate_series(1, 12) AS g;

-- 1 shop mới đang chờ duyệt (để demo nút Duyệt/Cấm).
INSERT INTO stores (name, description, owner_id, store_level_id, approved, created_at, updated_at)
SELECT 'Hoa Xinh Gò Vấp', 'Shop mới đăng ký, chờ Admin duyệt', u.id,
       (SELECT id FROM store_levels WHERE name = 'Cơ bản' LIMIT 1), FALSE,
       now() - interval '2 days', now() - interval '2 days'
FROM users u WHERE u.email = 'vendor@starshop.vn';

-- 24 đơn demo trong 24 ngày gần nhất, đủ các trạng thái, dùng 5 sản phẩm mẫu của V2.
WITH p AS (
    SELECT id, price, store_id, row_number() OVER (ORDER BY id) AS rn
    FROM (SELECT DISTINCT ON (name) id, name, price, store_id
          FROM products
          WHERE name IN ('Bó hồng đỏ 20 bông', 'Kệ hoa khai trương', 'Hoa cưới cầm tay',
                         'Bó hướng dương', 'Lẵng hoa chia buồn')
          ORDER BY name, id) x
),
g AS (
    SELECT n,
           (ARRAY['DELIVERED','DELIVERED','DELIVERED','SHIPPING','CONFIRMED','NEW','CANCELLED','DELIVERED','RETURNED','PICKED_UP'])[1 + n % 10] AS status,
           1 + n % 3 AS qty
    FROM generate_series(1, 24) AS n
)
INSERT INTO orders (code, buyer_id, store_id, status, payment_method, paid,
                    subtotal, shipping_fee, discount, total, shipping_address, created_at, updated_at)
SELECT 'DEMO-TV3-' || lpad(g.n::text, 3, '0'),
       (SELECT id FROM users WHERE email = 'user@starshop.vn'),
       p.store_id,
       g.status,
       CASE WHEN g.n % 2 = 0 THEN 'COD' ELSE 'VNPAY' END,
       (g.n % 2 = 1 AND g.status NOT IN ('CANCELLED')) OR g.status = 'DELIVERED',
       p.price * g.qty,
       20000,
       0,
       p.price * g.qty + 20000,
       '12 Nguyễn Huệ, Phường Bến Nghé, Quận 1, TP.HCM',
       now() - make_interval(days => g.n - 1, hours => g.n % 6),
       now() - make_interval(days => g.n - 1, hours => g.n % 6)
FROM g JOIN p ON p.rn = 1 + (g.n % 5);

-- Dòng sản phẩm tương ứng (chốt unit_price = giá lúc mua).
WITH p AS (
    SELECT id, price, row_number() OVER (ORDER BY id) AS rn
    FROM (SELECT DISTINCT ON (name) id, name, price
          FROM products
          WHERE name IN ('Bó hồng đỏ 20 bông', 'Kệ hoa khai trương', 'Hoa cưới cầm tay',
                         'Bó hướng dương', 'Lẵng hoa chia buồn')
          ORDER BY name, id) x
)
INSERT INTO order_items (order_id, product_id, quantity, unit_price, created_at, updated_at)
SELECT o.id, p.id, 1 + (n % 3), p.price, o.created_at, o.created_at
FROM (SELECT id, created_at, substring(code FROM 10)::int AS n FROM orders WHERE code LIKE 'DEMO-TV3-%') o
JOIN p ON p.rn = 1 + (o.n % 5);
