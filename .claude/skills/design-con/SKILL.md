---
name: design-con
description: >
  Design-consistency — giữ giao diện StarShop NHẤT QUÁN theo design system, chống phình.
  Dùng khi tạo/sửa UI: trang, component, nút, icon, màu, form, bảng, layout; hoặc khi
  review giao diện. Skill này bắt đối chiếu bộ luật FE (rules/frontend/*) và registry
  SSOT (tokens.css, WEB-INF/tags, icon Lucide) TRƯỚC khi code, và kiểm sau khi code.
---

# design-con — Nhất quán thiết kế (design consistency)

**Mục tiêu:** mọi màn hình trông như **một hệ thống** — không lệch màu/size, không đẻ component/trang thừa.

## Luồng chuẩn
1. **Trước khi code UI:** mở đúng file luật theo chủ đề (router ở `CLAUDE.md`):
   - layout/shell → `rules/frontend/01-app-shell-layout.md`
   - màu/token → `02-color-token.md` · font/spacing → `03-typography-spacing.md`
   - icon → `04-icon.md` · component/trạng thái → `05-components-states.md`
2. **Chạy Cổng chống phình** `rules/frontend/06-anti-bloat.md` (4 câu). Chỉ tạo mới khi cả 4 = "không".
3. **Dùng SSOT:** màu/size = `var(--…)` (tokens.css); markup lặp = JSP tag (WEB-INF/tags); icon = registry Lucide (không emoji).
4. **Sau khi code:** chạy checklist `✅ Kiểm` cuối mỗi file luật liên quan.

## Vi phạm cần chặn (review)
- Mã hex/size viết cứng ngoài tokens.css.
- Emoji trong UI; icon copy-paste rải rác.
- `<table>`/`<form>` markup lặp thay vì dùng tag chung.
- Trang/URL trùng mục đích; layout mới không justify.
- Màn thiếu trạng thái loading/empty/error.

## Đầu ra khi review
Bảng: Vi phạm · Vị trí (file) · Luật bị phạm (§) · Cách sửa. Không đạt → trả lại sửa trước khi merge.

> Đây là bản "reviewer" của bộ luật FE; kết hợp `chuan-nganh` khi cần chốt một chuẩn thiết kế còn tranh cãi.
