---
name: postgresql-optimize
description: >
  Tối ưu PostgreSQL cho StarShop: query, table, index, view, materialized view (mv),
  phân trang, tránh N+1 (JPA). Dùng khi user nhắc "tối ưu", "chậm", "slow query",
  "EXPLAIN", "index", "view/mv", "N+1", "phân trang lớn", hoặc trước khi viết truy vấn
  báo cáo/thống kê nặng. Luôn ĐO trước-sau, không đoán.
---

# postgresql-optimize — Tối ưu Postgres (đo → sửa → đo lại)

**Nguyên tắc:** không tối ưu khi chưa đo. Dùng `EXPLAIN (ANALYZE, BUFFERS)` để thấy chi phí thật.

## Luồng chuẩn
1. **Xác định mục tiêu:** query / table / view / mv / index nào, đang chậm bao nhiêu.
2. **Đo:** `EXPLAIN (ANALYZE, BUFFERS)` → xem Seq Scan, rows, cost, thời gian, buffers.
3. **Chẩn đoán** theo checklist bên dưới.
4. **Sửa** (một thay đổi/lần) → **đo lại** → giữ nếu tốt hơn.
5. Ghi lại quyết định (vì sao index này, vì sao mv).

## Checklist tối ưu (theo thứ tự rẻ→đắt)
- **Index đúng chỗ:** FK + cột lọc/sắp xếp; composite theo thứ tự cột lọc; **partial** (`WHERE status='ACTIVE'`/`deleted_at IS NULL`); **GIN** cho mảng/full-text; tránh index thừa (chậm ghi).
- **Query rewrite:** tránh `SELECT *`; lọc sớm; `EXISTS` thay `IN (subquery)` khi hợp; tránh function trên cột trong WHERE (mất index).
- **Phân trang lớn:** ưu tiên **keyset pagination** (`WHERE id < :last ORDER BY id DESC LIMIT n`) thay `OFFSET` lớn.
- **View vs Materialized View:** báo cáo/thống kê nặng, đọc nhiều-ghi ít → **MV** + lịch `REFRESH` (hoặc `CONCURRENTLY`); dữ liệu realtime → view thường/query.
- **N+1 (JPA):** dùng `JOIN FETCH`/`@EntityGraph`/`fetch=LAZY` hợp lý; đừng để list gọi query từng phần tử.
- **Bảo trì:** `ANALYZE` sau nạp seed lớn; `VACUUM` khi cần; kiểm `pg_stat_statements` cho top query chậm.
- **Kiểu & schema:** đúng datatype (numeric cho tiền), chuẩn hóa 3NF nhưng chấp nhận cột tổng hợp (sold_count, rating_avg) có trigger cập nhật khi đọc quá nhiều.

## Áp cho StarShop (26 bảng)
- Trang chủ "top SP bán chạy": index `products(status, sold_count DESC)`.
- Lịch sử đơn theo trạng thái: index `orders(buyer_id, status)`.
- Thống kê doanh thu (Chart.js): cân nhắc **MV** `mv_revenue_daily` refresh theo ngày.
- Tìm kiếm SP theo tên: cân nhắc GIN + `pg_trgm` nếu cần LIKE nhanh.

> Kết hợp `chuan-nganh` khi cần chốt "cách chuẩn" cho một lựa chọn (vd MV vs cache).
