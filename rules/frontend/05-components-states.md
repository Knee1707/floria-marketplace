# LUẬT FE 05 — Components · States · Form · Table · Overlay

> Trigger: "button/nút", "badge", "trạng thái màn", "form", "table/bảng UI", "modal", "toast".
> Nguyên tắc: mọi component qua **tag dùng chung** (`WEB-INF/tags/`), không markup lặp.

## 1. Button
- Variant: `primary · accent · outline · ghost · danger`; size `sm/md/lg`.
- **6 trạng thái/variant:** default · hover · focus · active · disabled · loading.
- Dùng `<t:button>`; nút hành động cao ≥ 44px (touch).

## 2. StatusBadge (SSOT trạng thái — 1 trạng thái = 1 màu toàn site)
Truyền enum thô, KHÔNG tự đặt màu. Dùng `status-badge.tag`.
| Đơn | NEW→info · CONFIRMED→primary · PICKED_UP→accent · SHIPPING→warning · DELIVERED→success · CANCELLED→neutral · RETURNED→danger |
| SP | ACTIVE→success · HIDDEN→neutral · BANNED→danger |
| Thanh toán | paid→success · chưa→warning |
Giá trị lạ → chuỗi gốc + xám (fail-safe).

## 3. Trạng thái mỗi màn (đủ 4+1, dùng `empty-state.tag`)
`loading`(skeleton) · `empty`(icon + câu ngắn + **nút hành động**) · `error`(icon danger + **nút thử lại**) · `offline`(banner, cho realtime) · `success`(dữ liệu). **Không màn trắng.**

## 4. Form (dùng `form-field.tag`)
- Mẫu tái dùng: login · register+OTP · forgot/reset · địa chỉ nhận hàng · CRUD sản phẩm · checkout.
- Field: label `for/id` + hint tooltip "i" + lỗi dưới field.
- Validation 2 lớp: client (Bootstrap) + server (`@Valid`) — vd comment ≥50 ký tự, sđt hợp lệ.
- Nút submit có trạng thái `loading`. Export Excel/PDF: 1 helper chung, icon registry.

## 5. Table/Matrix (dùng `data-table.tag`)
- Column header 14px đậm · sticky khi cuộn · zebra nhẹ · số căn phải, tiền tách nghìn.
- Có sort (header), lọc (đầu bảng), phân trang (dưới), empty "Chưa có dòng nào" + nút.
- Dashboard số liệu: `stat-tile.tag` + Chart.js (đường/cột/tròn).

## 6. Overlay chung
Modal (`modal.tag`, đóng backdrop/Esc, focus trap) · Toast (thành công/lỗi) · Tooltip "i" (hướng dẫn dài, hover). Không tự chế popup riêng.

## 7. Phong cách Minimal
Ít chữ; hướng dẫn dài → tooltip "i"; phân cấp bằng cỡ chữ/khoảng cách hơn là viền.

## ✅ Kiểm
- [ ] Button/badge/table/form/modal đều qua tag chung?
- [ ] Mỗi màn có đủ loading/empty/error/success?
- [ ] StatusBadge lấy màu theo SSOT, không tự chế?
