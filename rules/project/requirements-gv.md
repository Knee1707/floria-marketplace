# LUẬT PROJECT — Yêu cầu Giáo viên & Barem (bám sát để không mất điểm)

> Trigger: "yêu cầu giáo viên", "barem", "chấm điểm", "role nào làm gì".
> Đây là chân lý về **phạm vi chấm điểm**. Làm chức năng phải soi lại đây.

## 1. Công nghệ bắt buộc
Spring Boot + JSP/JSTL + Bootstrap + JPA + **PostgreSQL** + Decorator **SiteMesh** + **JWT** + **WebSocket** + **Cloudinary**. Auth dùng **Spring Security**. Mật khẩu mã hoá. 3 tầng.

## 2. Chức năng CHUNG (mọi role)
Tìm kiếm & lọc SP · đăng ký có **OTP email** kích hoạt · đăng nhập/xuất · quên mật khẩu có **OTP email**.

## 3. Chức năng theo role
- **Guest:** trang chủ hiển thị **>10 SP** của các shop, sắp xếp **giảm dần**.
- **User:** trang chủ; SP theo danh mục; **20 SP** (mới/bán chạy/đánh giá/yêu thích) **phân trang/lazy**; profile + **nhiều địa chỉ nhận hàng**; chi tiết SP; **giỏ hàng lưu DB**; **thanh toán COD/VNPAY/MOMO**; lịch sử đơn theo trạng thái (mới/đã xác nhận/đang giao/đã giao/huỷ/trả-hoàn); thích SP; SP đã xem; **đánh giá + bình luận (text ≥50 ký tự, ảnh/video)** SP đã mua; chọn mã giảm giá.
- **Vendor:** quyền User + đăng ký shop, quản lý trang chủ shop, quản lý SP, **quản lý đơn shop** (mới/đã xác nhận/đã lấy/đang giao/đã giao/huỷ/trả-hoàn), tạo khuyến mãi, quản lý doanh thu.
- **Manager:** quản lý từng cửa hàng: tìm & quản lý user, SP từng shop, danh mục, **chiết khấu app**, khuyến mãi (giảm % SP / phí ship), nhà vận chuyển (tên + phí).
- **Admin:** như Manager nhưng **toàn hệ thống**.
- **Shipper:** quản lý đơn được phân công giao, thống kê đơn đã giao.

## 4. Barem (100 → 10)
| Tiêu chí | Điểm |
|---|---|
| Quản lý, phân công + commit GitHub | 1.0 |
| Đúng cấu trúc 3 tầng | 0.5 |
| Logic + validation + security đầy đủ theo role | 6.0 |
| Giao diện đẹp + **Responsive** | 1.0 |
| Chức năng sáng tạo (VNPAY, JWT, voucher, WebSocket…) | 1.0 |
| Báo cáo & trả lời câu hỏi | 0.5 |

## 5. Việc cần chốt với GV
Ranh giới **Manager vs Admin** (đề mô tả gần trùng → đề xuất Admin=toàn hệ thống, Manager=shop được phân công); phạm vi **Shipper** (không có trong file phân tích gốc); số cổng thanh toán.

## ✅ Kiểm
- [ ] Chức năng đang làm có trong danh sách role tương ứng?
- [ ] Có chạm tiêu chí barem nào (validation/security/responsive/sáng tạo)?
