# LUẬT DB 03 — Datatype · ERD

> Trigger: "kiểu dữ liệu", "datatype", "ERD", "quan hệ", "data modelling".

## 1. Datatype chuẩn
| Mục đích | Kiểu | Cấm |
|---|---|---|
| Khóa chính | `bigserial` (hoặc `uuid`) | — |
| Thời gian | `timestamptz` | `timestamp` không tz |
| Tiền/giá | `numeric(12,2)` | `float`/`real` |
| Phần trăm | `numeric(5,2)` | — |
| Danh sách ngắn cố định | `text[]` | chuỗi CSV |
| Dữ liệu mở rộng | `jsonb` | text chứa JSON |
| Cờ | `boolean` | int 0/1 |
| Enum | `varchar` + CHECK (JPA `@Enumerated(STRING)`) | int mã hoá |

## 2. ERD & 3NF (chấm điểm nặng)
- Vẽ đủ 3 mức: khái niệm → logic → vật lý. ERD Mermaid: xem `../../../PHAN_CONG.md` §9.7.
- Xương sống: `users → stores → products → orders → order_items`; nhánh `delivery_assignments`, `reviews/review_media`, `vouchers/order_vouchers`, `notifications`.
- Chuẩn hóa: thông tin lặp (tên danh mục/shop, giá) tách bảng + FK; giá mua chốt ở `order_items.unit_price` (không đổi theo giá SP sau).
- **LUẬT:** thêm bảng → cập nhật ERD + giải thích quan hệ + thuyết minh 3NF trong báo cáo.

## ✅ Kiểm
- [ ] Đúng datatype (tiền numeric, thời gian timestamptz)?
- [ ] ERD cập nhật khi đổi schema?
