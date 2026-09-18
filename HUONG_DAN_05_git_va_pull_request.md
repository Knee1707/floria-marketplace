# HƯỚNG DẪN 05 — Git & Pull Request

> Barem "quản lý + commit GitHub" = 1đ. Làm đúng quy trình để không mất điểm và không đụng code nhau.

## 1. Nhánh
- `main` — chỉ code chạy được (để demo/nộp). KHÔNG push thẳng.
- `develop` — nhánh tích hợp.
- `feature/{tv}-{leaf}` — mỗi việc 1 nhánh. Vd `feature/tv2-f4.3-crud-sp`, `feature/tv3-f6.2-category`.

## 2. Vòng đời 1 việc
```
git checkout develop && git pull
git checkout -b feature/tv2-f4.3-crud-sp
# ... code trong file @owner của mình ...
git add -p && git commit -m "feat(vendor): CRUD sản phẩm cho shop"
git push -u origin feature/tv2-f4.3-crud-sp
# Mở Pull Request vào develop → gán Leader review → sửa nếu cần → Leader merge
```

## 3. Commit convention (Conventional Commits)
`type(scope): mô tả` — type: `feat, fix, refactor, docs, style, test`.
Ví dụ: `feat(order): vendor xác nhận đơn`, `fix(cart): tính lại tổng sau khi xoá`.
Commit **nhỏ, thường xuyên** (GV chấm quá trình commit).

## 4. Cơ sở dữ liệu
- Đổi schema = tạo **migration MỚI** `V{n}__mo_ta.sql` (n tăng dần). KHÔNG sửa V1/V2 đã chạy.
- Báo Leader trước khi merge migration để tránh trùng số.

## 5. Bảng tiến độ (Leader quản)
- GitHub **Projects (Kanban)**: mỗi leaf = 1 card Todo → Doing → Review → Done.
- Cập nhật `TASKS.md` khi xong.
- Đầu tuần: checkpoint 15’; cuối tuần: chốt %.

## 6. Luật review của Leader
- Đọc diff, chạy thử. Đối chiếu `CLAUDE.md` + rules (không hard-code/emoji, đúng 3 tầng, đủ trạng thái).
- Vi phạm → request changes, không merge. main luôn xanh (chạy được).

## 7. Cấm
- Push thẳng `main`. Sửa file `@owner` người khác không hỏi. Sửa `config/`, `domain/`, `tokens.css`, `pom.xml` khi không phải Leader. Commit bí mật (.env, mật khẩu).
