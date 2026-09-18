# LUẬT FE 01 — App Shell · Layout · Sitemap · Navigation

> Trigger: "layout", "app shell", "trang mới", "sitemap", "navigation", "menu".
> Nguyên tắc: **dùng chung app shell/layout. Chỉ vẽ layout mới khi justify được.**

## 1. Sitemap (một chức năng = một URL — không đẻ URL trùng mục đích)
```
KHÁCH/USER  /  ·  /products ·/products/{id}  ·  /categories/{slug}
            /cart · /checkout · /account/profile · /account/orders
            /auth/login · /register · /forgot
VENDOR   /vendor/**    dashboard, sản phẩm, đơn, khuyến mãi, ví/doanh thu
SHIPPER  /shipper/**   đơn phân công, cập nhật giao, thống kê
MANAGER  /manager/**   quản trị phạm vi shop được phân công
ADMIN    /admin/**     user, danh mục, chiết khấu, vận chuyển, thống kê
```
**Trang mới phải khai báo ở đây trước khi code.**

## 2. Chỉ 4 App Shell (kế thừa 1 `default.jsp` SiteMesh) — KHÔNG đẻ shell thứ 5
| Shell | Dùng cho | Cấu trúc |
|---|---|---|
| Public | Guest + User | Top bar · Main bar (logo, search, giỏ, CTA) · Nav ngang · Content · Footer · CTA đáy (mobile) |
| Vendor | Vendor | Side nav trái · Topbar · Content |
| Admin | Admin + Manager | Side nav trái · Topbar · Content (dashboard) |
| Shipper | Shipper | Topbar gọn · list đơn · Content (mobile-first) |

## 3. Tái dùng markup = JSP tag (KHÔNG copy-paste)
Đặt tại `WEB-INF/tags/`: `page-header.tag`, `card.tag`, `status-badge.tag`, `data-table.tag`, `empty-state.tag`, `form-field.tag`, `pagination.tag`, `stat-tile.tag`, `modal.tag`.
Trang chỉ viết phần nội dung `<body>`; layout do SiteMesh bọc.

## 4. Navigation
- 1 shell = 1 kiểu điều hướng chính (top cho public, side cho vendor/admin, bottom-CTA cho mobile).
- Mục active tô `--color-accent`. Không chồng nhiều thanh điều hướng.

## 5. Cảnh báo kỹ thuật
SiteMesh 3 chạy Tomcat **10.1** (không phải 11); decorator ở `WEB-INF/views/decorators/default.jsp`; coi chừng lỗi decorator-prefix.

## ✅ Kiểm
- [ ] Trang mới có trong sitemap? nằm trong 1 trong 4 shell?
- [ ] Markup lặp đã tách thành `.tag`?
- [ ] Không tự vẽ layout mới khi shell có sẵn phủ được?
