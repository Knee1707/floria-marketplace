-- V6 — Đổi tên hiển thị + email tài khoản demo theo yêu cầu.
-- GIỮ NGUYÊN dữ liệu liên quan (id không đổi -> đơn hàng/cửa hàng vẫn liên kết).
-- Idempotent: match theo email CŨ, chạy lại lần sau sẽ không khớp -> no-op.
-- Mật khẩu KHÔNG đổi (vẫn 123456). Email dạng Ten@starshop.vn (không dấu).

-- ===== Nhân sự =====
UPDATE users SET email='KhangDi@starshop.vn',   full_name='Khang Di'  WHERE email='admin@starshop.vn';
UPDATE users SET email='NhatKhoa@starshop.vn',  full_name='Nhật Khoa' WHERE email='vendor@starshop.vn';
UPDATE users SET email='PhongKhoa@starshop.vn', full_name='Phong Khoa' WHERE email='shipper@starshop.vn';
UPDATE users SET email='TrungDuc@starshop.vn',  full_name='Trung Đức' WHERE email='manager@starshop.vn';

-- ===== Người mua / khách (role USER) =====
UPDATE users SET email='Son@starshop.vn',   full_name='Sơn'   WHERE email='user@starshop.vn';
UPDATE users SET email='Huy@starshop.vn',   full_name='Huy'   WHERE email='khach01@starshop.vn';
UPDATE users SET email='Khang@starshop.vn', full_name='Khang' WHERE email='khach02@starshop.vn';
UPDATE users SET email='Huong@starshop.vn', full_name='Hương' WHERE email='khach03@starshop.vn';
UPDATE users SET email='Bui@starshop.vn',   full_name='Bùi'   WHERE email='khach04@starshop.vn';
UPDATE users SET email='Minh@starshop.vn',  full_name='Minh'  WHERE email='khach05@starshop.vn';
UPDATE users SET email='Ly@starshop.vn',    full_name='Lý'    WHERE email='khach06@starshop.vn';
UPDATE users SET email='Luan@starshop.vn',  full_name='Luân'  WHERE email='khach07@starshop.vn';
UPDATE users SET email='Thu@starshop.vn',   full_name='Thư'   WHERE email='khach08@starshop.vn';
UPDATE users SET email='Giau@starshop.vn',  full_name='Giàu'  WHERE email='khach09@starshop.vn';
UPDATE users SET email='Trang@starshop.vn', full_name='Trang' WHERE email='khach10@starshop.vn';
UPDATE users SET email='Nhi@starshop.vn',   full_name='Nhi'   WHERE email='khach11@starshop.vn';
UPDATE users SET email='Linh@starshop.vn',  full_name='Linh'  WHERE email='khach12@starshop.vn';
