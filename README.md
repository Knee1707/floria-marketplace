# StarShop — Website bán hoa (Spring Boot)

Bộ khung do **Leader (TV1)** dựng theo tài liệu `StarShop_MECE_Workflow.md`.
Kiến trúc **3 tầng** (Controller → Service → Repository), JSP + SiteMesh 3, PostgreSQL + Flyway,
Spring Security (session) + JWT (REST/WebSocket), WebSocket, Cloudinary.

## 1. Yêu cầu môi trường
- JDK **17** (Temurin 17.0.13)
- Maven **3.9.9** (hoặc dùng IDE)
- PostgreSQL **16** đang chạy, tạo sẵn database rỗng tên `starshop`:
  ```sql
  CREATE DATABASE starshop;
  ```

## 2. Cấu hình biến môi trường (không commit bí mật)
Đặt trước khi chạy (PowerShell ví dụ):
```powershell
$env:DB_USER="postgres"; $env:DB_PASS="postgres"
$env:MAIL_USER="your@gmail.com"; $env:MAIL_APP_PASSWORD="app-password-16-ky-tu"
$env:JWT_SECRET="chuoi-bi-mat-that-dai-tren-32-byte"
# Cloudinary + VNPAY khi làm tới F4.3 / F3.11
```
> Không đặt thì app vẫn chạy được phần Home/Auth (trừ gửi mail thật cần MAIL_*).

## 3. Chạy
```bash
mvn spring-boot:run
```
Mở http://localhost:8080 → thấy trang chủ với 5 sản phẩm hoa seed sẵn.
Flyway tự tạo 26 bảng (`V1`) + seed dữ liệu (`V2`) ở lần chạy đầu.

## 4. Tài khoản demo (mật khẩu tất cả: `123456`)
| Role | Email |
|---|---|
| USER | user@starshop.vn |
| VENDOR | vendor@starshop.vn |
| MANAGER | manager@starshop.vn |
| ADMIN | admin@starshop.vn |
| SHIPPER | shipper@starshop.vn |

## 5. Đóng gói WAR
```bash
mvn clean package    # -> target/starshop.war (deploy Tomcat 10.1)
```

## 6. Đã chạy được sẵn (mẫu tham chiếu)
- Trang chủ `/` (F3.1) — controller + repository + JSP hoàn chỉnh.
- Đăng ký + OTP email (F2.1, F2.2), đăng nhập/đăng xuất (F2.3), phân quyền 6 role (F2.6).

## 7. Thành viên làm gì tiếp
Xem `TASKS.md`. Nguyên tắc: **chỉ điền body vào file có `@owner` đúng tên mình**, không sửa
`config/`, `domain/`, `default.jsp`, `pom.xml`. Cần đổi entity/route → báo Leader.

## ⚠️ Việc Leader cần verify khi chạy máy thật
1. **SiteMesh taglib**: `default.jsp` dùng `uri="http://www.opensymphony.com/sitemesh/decorator"`
   với thẻ `<sitemesh:write property="title|head|body"/>`. Nếu bản SiteMesh 3.1.0 báo không thấy
   taglib, kiểm tra TLD trong jar và chỉnh URI cho khớp (đây là điểm dễ vướng nhất).
2. Lần đầu chạy: đảm bảo DB `starshop` rỗng để Flyway migrate sạch.
