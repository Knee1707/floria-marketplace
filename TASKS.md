# TASKS — Bảng tiến độ StarShop

Trạng thái: ⬜ Todo · 🟨 Doing · 🟦 Review · ✅ Done. Leader cập nhật cuối tuần.

## Leader (TV1) — Mua & Nền tảng
| Leaf | Mô tả | File chính | TT |
|---|---|---|---|
| F1.1–F1.5 | Khung + config + Flyway | `config/`, `pom.xml`, `V1/V2.sql` | ✅ |
| F2.1–F2.3 | Đăng ký/OTP/Login/Logout | `controller/auth`, `service/auth` | ✅ |
| F2.4 | Quên mật khẩu (OTP) | `AuthController` (TODO) | ⬜ |
| F2.6 | Phân quyền 6 role | `SecurityConfig` | ✅ |
| F2.7 | JWT REST/WebSocket | `security/JwtConfig` (TODO) | ⬜ |
| F2.8 | Bảo mật nâng cao | rải rác | ⬜ |
| F3.1 | Trang chủ | `HomeController` + `customer/home.jsp` | ✅ |
| F3.2–F3.17 | Tìm kiếm→giỏ→checkout→đơn→đánh giá | `controller/customer` (TODO) | ⬜ |

## TV2 — Bán & Giao vận  (chỉ sửa file @owner:TV2)
| Leaf | Mô tả | Stub sẵn | TT |
|---|---|---|---|
| F4.1–F4.7 | Vendor: shop, SP, đơn, KM, ví, doanh thu | `controller/vendor/*`, `service/vendor/*`, `views/vendor/*`, `V4__seed_vendor.sql` | ✅ |
| F5.1–F5.3 | Shipper: đơn phân công, cập nhật, thống kê | `controller/shipper/ShipperController`, `service/shipper/ShipperServiceImpl`, `views/shipper/*` | ✅ |
| F7.1–F7.3 | WebSocket + Cloudinary | `NotificationServiceImpl`, `CloudinaryServiceImpl`, `ChatController` | ✅ |

## TV3 — Quản trị & Giao diện  (chỉ sửa file @owner:TV3)
| Leaf | Mô tả | Stub sẵn | TT |
|---|---|---|---|
| F6.1–F6.10 | Admin/Manager: user, danh mục, thuộc tính SP, duyệt shop/SP, commission, KM hệ thống, vận chuyển, dashboard | `controller/admin`, `controller/manager`, `service/admin`, `views/admin`, `V5__seed_admin.sql` | 🟦 |
| F8.1–F8.3 | Template Bootstrap, layout, responsive | `decorators/default.jsp`, shell Admin `tags/admin-shell.tag` | 🟨 |

## Quy ước Git
- Nhánh: `feature/{tv}-{leaf}` → PR vào `develop` → Leader review → merge.
- Commit: `feat(scope): ...`, `fix(scope): ...`.
