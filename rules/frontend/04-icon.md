# LUẬT FE 04 — Icon (Claude-Icon)

> Trigger: "Icon", "biểu tượng", "emoji", "stroke", "svg".
> **Luật cứng:** KHÔNG dùng emoji trong UI. Chỉ dùng **Lucide** hoặc **tự vẽ** (khi Lucide không có).

## 1. Chuẩn Lucide
- `stroke-width: 1.5px`, `stroke: currentColor` (theo màu chữ), size 16/20/24 theo ngữ cảnh.
- Gọi qua **registry semantic** (theo Ý NGHĨA, không theo tên hình):
  `cart, search, filter, sort, add, edit, delete, view, upload, bell(thông báo), user, store, truck(giao hàng), chart, check, x, chevron`.

## 2. Tự vẽ (chỉ khi cần)
- Logo, hoa đặc thù mà Lucide không có → lưu `assets/icons/`, thêm vào registry, giữ stroke 1.5 + currentColor cho đồng bộ.

## 3. Chống phình (quan trọng)
- KHÔNG sinh icon rải rác kiểu `getArrowIcon()` / copy SVG khắp nơi. **Mọi icon qua 1 registry duy nhất.**
- Icon trang trí → `aria-hidden="true"`. Icon mang nghĩa → có `aria-label`.

## ✅ Kiểm
- [ ] Không emoji trong JSP/UI?
- [ ] Icon lấy từ registry Lucide (hoặc tự vẽ đã đăng ký)?
- [ ] stroke 1.5 + currentColor?
