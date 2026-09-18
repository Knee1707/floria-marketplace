# LUẬT FE 03 — Typography · Spacing

> Trigger: "font", "size chữ", "padding", "margin", "spacing", "row padding".

## 1. Typography
- 1 họ font (vd Inter / Be Vietnam Pro) + fallback hệ thống. Không trộn >2 font.
- Type scale (token, base 16px):

| Vai trò | Token | px |
|---|---|---|
| Hero/Display | `--text-4xl` | 40–48 |
| H1 | `--text-3xl` | 30 |
| H2 / Header | `--text-2xl` | 24 |
| H3 / Sub-header | `--text-xl` | 20 |
| Column header | `--text-sm` 600 | 14 |
| Body (mặc định) | `--text-base` | 16 |
| Caption/meta | `--text-xs` | 12 |

**LUẬT:** body ≥ **14px**, caption ≥ **12px** (a11y — không dùng 9px như phác thảo vì quá nhỏ). Weight chỉ 400/500/600/700. Chỉ lấy size từ scale.

## 2. Spacing (thang 4px)
`--space-1=4 … 2=8 3=12 4=16 6=24 8=32`.
- Row padding (dòng bảng/list): dọc 12 mặc định, gọn 8.
- Gutter nội dung 2 bên ≥ 16px.
- **LUẬT:** không dùng số lẻ (7px, 13px); mọi padding/margin từ thang.

## ✅ Kiểm
- [ ] Size chữ có trong scale? body ≥14, caption ≥12?
- [ ] Padding/margin lấy từ thang 4px?
