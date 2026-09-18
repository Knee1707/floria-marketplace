# LUẬT FE 06 — Cổng chống phình (chạy TRƯỚC khi tạo mới)

> Trigger: sắp tạo trang / component / nút / icon / màu / biến CSS mới.

## Cổng 4 câu — chỉ tạo mới khi CẢ 4 = "không"
- [ ] Đã có **shell/layout** phủ được trang này? → có: dùng lại.
- [ ] Đã có **tag/component** làm việc này (card, badge, table, form-field, empty-state…)? → có: dùng lại.
- [ ] Màu/size/spacing đã có **token**? → có: dùng `var(--…)`.
- [ ] Icon đã có trong **registry Lucide**? → có: dùng lại.

## Nếu buộc tạo mới
1. Đặt vào đúng nơi tập trung (tokens.css / WEB-INF/tags / icon registry).
2. Đặt tên theo chuẩn, thêm vào **registry SSOT** để lần sau tái dùng.
3. Ghi justify ngắn trong PR (vì sao thứ cũ không đủ).

## Dấu hiệu "phình" phải chặn ở review
- Mã hex/size viết cứng rải rác · emoji · markup `<table>`/`<form>` copy-paste · icon sinh vô tội vạ (`getXIcon`) · trang/URL trùng mục đích · layout mới không justify.

> Triết lý: **code → token → registry**. Một khái niệm hình ảnh chỉ tồn tại **một** chỗ.
