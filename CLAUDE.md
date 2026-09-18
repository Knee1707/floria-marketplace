# CLAUDE.md — Memory Index (HOOK) của StarShop

> **File này TỰ ĐỘNG được nạp** khi làm việc trong repo. Nó là **bộ định tuyến (router)**: gặp chủ đề nào thì BẮT BUỘC mở đúng file luật rồi mới code. Mục tiêu: **để mình không quên, để AI không quên** — và **chống phình** FE/DB.
>
> Đề tài: **StarShop** — web bán hoa chuỗi cửa hàng. Stack: Spring Boot 3.3.5 + JSP/JSTL + Bootstrap + JPA + PostgreSQL + SiteMesh + Spring Security + JWT + WebSocket + Cloudinary.

---

## 0. LUẬT VẬN HÀNH INDEX (đọc trước tiên)

1. **Trước khi code / sửa bất kỳ thứ gì**, dò bảng §1 theo từ khóa → mở file luật → làm đúng luật đó.
2. **3 nguyên tắc tối cao:** `Reuse > Create` · `Token > Hard-code` · `MECE (1 nguồn chân lý)`.
3. Không tạo mới (trang/component/nút/icon/màu/bảng/cột) nếu chưa qua **Cổng chống phình** (`rules/frontend/06-anti-bloat.md` hoặc `rules/database/02-schema-antibloat.md`).
4. Không chắc yêu cầu chấm điểm → mở `rules/project/requirements-gv.md`.

---

## 1. BẢNG ĐỊNH TUYẾN (từ khóa → file luật)

| Khi nhắc tới / đang làm… | ĐỌC file |
|---|---|
| "Icon", "biểu tượng", "emoji", stroke, svg | `rules/frontend/04-icon.md` |
| "màu", "color", "token", "hex", "theme" | `rules/frontend/02-color-token.md` |
| "layout", "app shell", "trang mới", "sitemap", "navigation", "menu" | `rules/frontend/01-app-shell-layout.md` |
| "font", "size chữ", "padding", "margin", "spacing", "radius", "bo góc" | `rules/frontend/03-typography-spacing.md` |
| "button", "nút", "badge", "trạng thái màn", "form", "table", "bảng UI", "modal", "toast" | `rules/frontend/05-components-states.md` |
| Tạo mới component/trang FE (kiểm phình) | `rules/frontend/06-anti-bloat.md` |
| "đặt tên", "naming", "tên bảng/cột", "camelCase" | `rules/database/01-naming.md` |
| "bảng mới", "cột mới", "tách bảng", "3NF", "đẻ bảng", "phình bảng" | `rules/database/02-schema-antibloat.md` |
| "kiểu dữ liệu", "datatype", "ERD", "quan hệ" | `rules/database/03-datatype-erd.md` |
| "bảo mật", "quyền", "RLS", "Spring Security", "index", "khóa ngoại", "ràng buộc" | `rules/database/04-security-index.md` |
| "doanh thu", "sold_count", "rating", "metric", "ảnh", "video", "upload", "cloudinary" | `rules/database/05-metrics-media.md` |
| "yêu cầu giáo viên", "barem", "chấm điểm", "role nào làm gì" | `rules/project/requirements-gv.md` |
| "phân công", "ai làm gì" | `../PHAN_CONG.md` |

> Quy ước giống ví dụ của bạn: *gặp "Icon" → đọc file luật Icon* trước khi động vào code.

---

## 2. NGUỒN CHÂN LÝ (SSOT) — không sửa rải rác

| Thứ | Chỉ định nghĩa 1 nơi |
|---|---|
| Màu / size / spacing / radius | `src/main/webapp/assets/css/tokens.css` |
| Component UI dùng lại | `src/main/webapp/WEB-INF/tags/*.tag` |
| Icon | registry Lucide (xem `rules/frontend/04-icon.md`) |
| Trạng thái (đơn hàng, SP…) | enum + `status-badge.tag` |
| Schema DB | Flyway `src/main/resources/db/migration/V1__init.sql` (ddl-auto=none) |
| Metric (doanh thu, sold_count…) | `rules/database/05-metrics-media.md` |

---

## 2b. SKILLS (quy trình lặp lại — gọi khi gặp việc tương ứng)

> Skill = việc lặp đi lặp lại, có nguyên tắc + luồng chung. Định nghĩa ở `.claude/skills/`.

| Skill | Khi nào dùng |
|---|---|
| `starshop-rules` | Trước khi code FE/DB — bắt đọc đúng file luật (router §1). |
| `chuan-nganh` | Cần chốt "cách chuẩn ngành" cho 1 quyết định kỹ thuật (thu ≥3 nguồn uy tín, đối chiếu số liệu, đề xuất hợp context). |
| `postgresql-optimize` | Tối ưu query/table/view/mv/index; slow query; N+1; phân trang lớn. |
| `design-con` | Giữ giao diện nhất quán theo design system; review UI chống phình. |

## 3. CÁCH KIỂM (máy kiểm / self-check trước khi commit)

- [ ] FE: không hex/size viết cứng (đều qua token)? không emoji? dùng shell/tag có sẵn?
- [ ] DB: tên snake_case tiếng Anh? không đẻ bảng/cột thừa? đúng 3NF? có index FK?
- [ ] Đúng phạm vi `@owner` của mình (xem PHAN_CONG §3)?
- [ ] Nếu buộc tạo mới → đã justify + thêm vào registry SSOT chưa?

*Router hết. Chi tiết nằm trong `rules/`.*
