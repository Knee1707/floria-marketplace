# LUẬT DB 01 — Naming Convention (thống nhất)

> Trigger: "đặt tên", "naming", "tên bảng/cột", "camelCase", "lúc Việt lúc Anh".
> **Một ngôn ngữ, một kiểu.** DB dùng **tiếng Anh + snake_case**. Cấm trộn tiếng Việt / CamelCase trong DB.

| Đối tượng | Quy ước | Ví dụ |
|---|---|---|
| Bảng | snake_case, **số nhiều** | `users`, `order_items`, `delivery_assignments` |
| Cột | snake_case | `full_name`, `sold_count`, `created_at` |
| Khóa chính | `id` (`bigserial`/`uuid`) | `id` |
| Khóa ngoại | `<đơn>_id` | `store_id`, `buyer_id` |
| Boolean | `is_/has_/paid/enabled` | `is_featured`, `paid` |
| Enum value | UPPER_SNAKE | `NEW`, `CONFIRMED` |
| Index | `idx_<bảng>_<cột>` | `idx_products_status` |
| CHECK | `ck_<bảng>_<cột>` | `ck_orders_status` |

- Java/JPA: bảng snake_case ↔ field camelCase (`store_id`↔`storeId`); Entity PascalCase số ít (`OrderItem`).
- **LUẬT:** một khái niệm = một tên duy nhất. Không để `customer` và `user` cùng nghĩa khác tên.

## ✅ Kiểm
- [ ] Tất cả bảng/cột snake_case tiếng Anh?
- [ ] Không có tên lẫn lộn Việt/Anh/Camel?
- [ ] FK theo mẫu `<đơn>_id`?
