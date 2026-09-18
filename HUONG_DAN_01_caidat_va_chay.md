# HƯỚNG DẪN 01 — Cài đặt & Chạy StarShop

> Đọc CLAUDE.md (router luật) trước khi code. File này giúp mọi máy chạy được project trong 10 phút.

## 1. Cần cài sẵn
- **JDK 17** (Temurin 17.0.13). Kiểm: `java -version`.
- **Maven 3.9+** (hoặc dùng IDE IntelliJ/Eclipse có sẵn Maven).
- **PostgreSQL 16** đang chạy. Tạo database rỗng:
  ```sql
  CREATE DATABASE starshop;
  ```

## 2. Biến môi trường (KHÔNG commit bí mật)
Đặt trước khi chạy (PowerShell):
```powershell
$env:DB_USER="postgres"; $env:DB_PASS="matkhau_postgres_cua_ban"
$env:MAIL_USER="email@gmail.com"; $env:MAIL_APP_PASSWORD="app-password-16-ky-tu"
$env:JWT_SECRET="chuoi-bi-mat-dai-tren-32-byte-tuy-y"
# Cloudinary + VNPAY: đặt khi làm F4.3 / F3.11
```
> Không đặt MAIL_* thì phần gửi OTP thật sẽ lỗi; các phần khác vẫn chạy.

## 3. Chạy
```bash
mvn spring-boot:run
```
Mở http://localhost:8080 → trang chủ có 5 sản phẩm hoa (seed sẵn). Flyway tự tạo 26 bảng (V1) + seed (V2) ở lần chạy đầu.

## 4. Tài khoản demo (mật khẩu tất cả: `123456`)
`user@starshop.vn` · `vendor@starshop.vn` · `manager@starshop.vn` · `admin@starshop.vn` · `shipper@starshop.vn`

## 5. Đóng gói WAR (deploy Tomcat 10.1)
```bash
mvn clean package    # -> target/starshop.war
```

## 6. Lỗi hay gặp
| Triệu chứng | Nguyên nhân / cách sửa |
|---|---|
| JSP tải về dạng file, không render | thiếu `tomcat-embed-jasper` hoặc packaging ≠ war (đã cấu hình sẵn — đừng đổi) |
| `relation ... does not exist` | DB chưa migrate — xoá DB tạo lại rỗng, chạy lại để Flyway migrate |
| Lỗi taglib `sitemesh` | kiểm URI trong `views/decorators/default.jsp` khớp SiteMesh 3.1.0 |
| `LazyInitializationException` trong JSP | thiếu `@EntityGraph`/JOIN FETCH ở repository (open-in-view=false — xem rules/database) |
| Sai mật khẩu khi đăng nhập demo | mật khẩu là `123456` (BCrypt trong V2) |

## 7. Trước khi bắt tay code
1. Đọc `CLAUDE.md` → dò router.
2. Đọc `HUONG_DAN_02_quyuoc.md`.
3. Xem file hướng dẫn của mình: TV2 → `HUONG_DAN_03_TV2.md`, TV3 → `HUONG_DAN_04_TV3.md`.
4. Git theo `HUONG_DAN_05_git.md`.
