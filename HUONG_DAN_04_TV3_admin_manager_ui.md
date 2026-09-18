# HƯỚNG DẪN 04 — TV3: Manager/Admin + Giao diện

> Viết vào ĐÂU: package/thư mục `@owner: TV3`. Viết THẾ NÀO: bảng dưới.
> Đọc trước: `HUONG_DAN_02_quy_uoc_code.md` + `CLAUDE.md`.

## 0. Bạn sở hữu
```
service/admin/       ← Impl cho interface Leader khai báo
controller/admin/    controller/manager/
views/admin/         views/manager/
Hoàn thiện giao diện chung: assets/css (thêm biến qua token), responsive.
  (Sửa default.jsp phải qua Leader.)
Flyway: V5__seed_admin.sql (danh mục, chiết khấu, vận chuyển, cấp độ)
```

## 1. Manager (1 shop được phân công) & Admin (toàn hệ thống) — D6
| Leaf | Ở đâu | Thế nào |
|---|---|---|
| F6.1 Quản lý user | `service/admin/UserAdminService` + `controller/admin/UserAdminController` + `views/admin/users.jsp` | `UserRepository` (thêm phân trang/tìm kiếm — nhờ Leader). Khoá/mở (`locked`), đổi role. Dùng `<t:data-table>` + `<t:pagination>`. |
| F6.2 Danh mục | `service/admin/CategoryService` + `controller/admin/CategoryController` + `views/admin/categories.jsp` | CRUD `Category` (phân cấp `parent_id`). |
| F6.3 Thuộc tính SP | `service/admin/StyleService` + `controller/admin/StyleController` + `views/admin/styles.jsp` | CRUD `Style` + `StyleValue`. |
| F6.4 Duyệt/cấm shop & SP | trong UserAdmin/Category controllers | đổi `Store.approved`, `Product.status` (Admin toàn hệ thống; Manager giới hạn shop được giao qua `store_managers`). |
| F6.5 Chiết khấu app | `service/admin/CommissionService` + `views/admin/commission.jsp` | CRUD `Commission` (% hoa hồng theo shop). |
| F6.6 Khuyến mãi hệ thống | `service/admin/SystemPromotionService` | tạo `Promotion`/`Voucher` với `store_id = null` (toàn sàn). |
| F6.7 Nhà vận chuyển | `service/admin/DeliveryService` + `views/admin/deliveries.jsp` | CRUD `Delivery` (tên + phí ship — dùng khi checkout của Leader tính tiền). |
| F6.8–6.10 Thống kê/Dashboard | `service/admin/StatsService` + `views/admin/dashboard.jsp` | Query doanh thu/user mới/shop mới theo thời gian → Chart.js. Dùng `<t:stat-tile>` cho KPI. Admin=toàn hệ thống, Manager=shop được giao. |

## 2. Giao diện & UX (D8 — bạn phụ trách chất lượng UI chung)
- Đảm bảo **responsive** mọi trang (barem 1đ): kiểm mobile + desktop.
- Thêm biến/tinh chỉnh trong `tokens.css`? → **báo Leader** (tokens là SSOT, Leader gác).
- Có thể thêm CSS component mới trong `main.css` nếu justify (đừng hard-code trong JSP).
- Trang `/style-guide` (tuỳ chọn) để soi component.

## 3. Luật khi làm
- Chỉ sửa file `@owner: TV3`. JSP dùng tag chung + đủ 4 trạng thái. Không emoji/hex cứng.
- Admin action nhạy cảm (khoá user, cấm shop) → confirm bằng `<t:modal>`.
- Xong leaf: `@status: DONE` + PR.

## 4. Seed của bạn (V5__seed_admin.sql)
Danh mục hoa (sinh nhật/khai trương/cưới/chia buồn) + style (màu, dịp) + commission + nhà vận chuyển + cấp độ user/shop.

> Dashboard tham khảo `<t:stat-tile>` + Chart.js. Chi tiết bảng: `../PHAN_CONG.md` §9.5.
