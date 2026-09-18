# LUẬT FE 02 — Color & Design Tokens (SSOT) · Shape

> Trigger: "màu", "color", "token", "hex", "theme", "radius", "bo góc".
> Nguyên tắc: **Token > Hard-code.** Chỉ 1 file chứa mã màu: `assets/css/tokens.css`. Nơi khác dùng `var(--…)`.

## 1. Token 3 tầng
`Primitive` (bảng màu) → `Semantic` (vai trò) → `Component` (`--btn-*`, `--card-*`, `--badge-*`).
Đổi giao diện = sửa **tầng Semantic**, không sửa rải rác. Ánh xạ vào biến Bootstrap (`--bs-primary`…).

## 2. Semantic bắt buộc
| Vai trò | Token |
|---|---|
| Chủ đạo (nút/nav/tiêu đề) | `--color-primary` (tông hoa StarShop) |
| Nhấn (link active, badge) | `--color-accent` |
| Nền / bề mặt | `--color-bg` / `--color-surface` |
| Chữ chính / phụ | `--color-text` / `--color-text-muted` |
| Viền | `--color-border` |
| Trạng thái (cố định) | `--success #16A34A` · `--warning #F59E0B` · `--danger #DC2626` · `--info #2563EB` |

## 3. Shape / Radius
`--radius-sm 6` · `--radius 10` · `--radius-md 14` · `--radius-lg 20` · `--radius-full`.
Nút/input = `--radius`; thẻ = `--radius-md`; avatar/chip = full. Không trộn nhiều kiểu bo trong 1 khối.

## ✅ Kiểm
- [ ] KHÔNG có mã hex/`rgb()` nào ngoài `tokens.css`?
- [ ] Component chỉ dùng `var(--…)`?
- [ ] Bo góc lấy từ thang radius?
