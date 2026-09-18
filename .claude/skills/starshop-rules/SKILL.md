---
name: starshop-rules
description: >
  Luật & ràng buộc bắt buộc khi code đồ án StarShop (web bán hoa Spring Boot +
  JSP/Bootstrap + JPA/PostgreSQL). Dùng skill này TRƯỚC khi làm bất kỳ việc gì về
  giao diện (layout, app shell, màu/token, icon, button, badge, form, table, trạng thái)
  hoặc cơ sở dữ liệu (đặt tên bảng/cột, tạo bảng/cột, ERD, datatype, index, bảo mật,
  metric, upload ảnh/video). Mục tiêu: chống phình element FE và chống đẻ/phình bảng DB,
  giữ 1 nguồn chân lý. Kích hoạt khi nhắc: icon, biểu tượng, emoji, màu, token, layout,
  app shell, font, spacing, button, badge, form, table, modal, naming, schema, ERD,
  datatype, RLS, Spring Security, index, metric, cloudinary, upload.
---

# StarShop — Rules Enforcer (máy kiểm chống phình)

Bạn đang code đồ án **StarShop**. Trước khi tạo/sửa code FE hoặc DB, **BẮT BUỘC** làm theo trình tự:

## 1. Định tuyến qua Index
Mở `CLAUDE.md` (gốc repo) → dùng **bảng định tuyến** để tìm đúng file luật theo từ khóa, rồi đọc file đó trong `rules/` trước khi code.

| Chủ đề | File luật |
|---|---|
| Icon / emoji / svg | `rules/frontend/04-icon.md` |
| Màu / token / theme | `rules/frontend/02-color-token.md` |
| Layout / app shell / sitemap / navigation | `rules/frontend/01-app-shell-layout.md` |
| Font / spacing / radius | `rules/frontend/03-typography-spacing.md` |
| Button / badge / form / table / modal / trạng thái | `rules/frontend/05-components-states.md` |
| Tạo mới FE (kiểm phình) | `rules/frontend/06-anti-bloat.md` |
| Naming DB | `rules/database/01-naming.md` |
| Tạo bảng/cột / 3NF | `rules/database/02-schema-antibloat.md` |
| Datatype / ERD | `rules/database/03-datatype-erd.md` |
| Bảo mật / index / ràng buộc | `rules/database/04-security-index.md` |
| Metric / ảnh / video | `rules/database/05-metrics-media.md` |
| Yêu cầu GV / barem | `rules/project/requirements-gv.md` |

## 2. 3 nguyên tắc tối cao (không vi phạm)
- **Reuse > Create** · **Token > Hard-code** · **MECE / 1 nguồn chân lý**.

## 3. Cổng chống phình (bắt buộc)
- FE: chưa qua 4 câu trong `rules/frontend/06-anti-bloat.md` → KHÔNG tạo component/trang/màu/icon mới.
- DB: chưa qua cổng trong `rules/database/02-schema-antibloat.md` → KHÔNG thêm bảng/cột.

## 4. Tự kiểm trước khi báo "xong"
Chạy checklist `✅ Kiểm` ở cuối mỗi file luật liên quan + mục §3 trong `CLAUDE.md`.

> Nếu buộc tạo mới thứ chưa có: đặt vào nơi SSOT (tokens.css / WEB-INF/tags / icon registry / Flyway), đặt tên đúng chuẩn, ghi justify trong PR.
