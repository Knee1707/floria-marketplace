# PROMPT & CẤU HÌNH CHUẨN — Floria (StarShop)

> **Dán file này** khi nhờ AI/người code Floria. Nó vừa là **luật tham chiếu**, vừa gom **mọi cấu hình chuẩn** để không lệch môi trường & không phình FE/DB.
> Chi tiết luật nằm ở `CLAUDE.md` (router) + `rules/`. File này là bản gộp nhanh.
> **3 nguyên tắc tối cao:** `Reuse > Create` · `Token > Hard-code` · `MECE (1 nguồn chân lý)`.

---

## 1. CHỐT CÔNG NGHỆ (pin version — cả nhóm dùng ĐÚNG số này)

| Thành phần | Version | Ghi chú |
|---|---|---|
| JDK (Temurin) | **17.0.13** | bắt buộc cho Spring Boot 3 |
| Maven | **3.9.9** | packaging = **war** |
| spring-boot-starter-parent | **3.3.5** | khoá toàn bộ starter (BOM) |
| Spring Framework | 6.1.14 (BOM) | lõi |
| Spring Security | 6.3.4 (BOM) | mật khẩu **BCrypt** |
| Hibernate (JPA) | 6.5.3.Final (BOM) | jakarta.persistence |
| Tomcat | 10.1.31 (BOM) | **10.1**, không phải 11 |
| PostgreSQL (server) | **16.4** | schema `starshop` |
| PostgreSQL driver | 42.7.4 (BOM) | — |
| Flyway | **10.20.1** | `V{n}__*.sql`, không sửa file cũ |
| JSTL API / impl | 3.0.0 / 3.0.1 | jakarta |
| SiteMesh | **3.1.0** | 1 `default.jsp` chung |
| JWT (jjwt) | **0.12.6** | REST /api/** + WebSocket |
| Cloudinary | cloudinary-http5 **1.39.0** | upload ảnh/video |
| Bootstrap / jQuery / Chart.js | 5.3.3 / 3.7.1 / 4.4.6 | qua WebJars |
| Lombok / MapStruct | 1.18.34 (BOM) / 1.6.3 | tiện ích |
| Thanh toán | **VNPAY sandbox** + COD | MOMO tuỳ chọn |

> **Quy tắc:** chỉ **Leader** được thêm/nâng dependency. Cần lib mới → mở issue → Leader duyệt → cả nhóm `mvn clean install`.

---

## 2. CẤU HÌNH MÔI TRƯỜNG (`application.yml` + biến môi trường)

**Bí mật KHÔNG commit** — đặt qua biến môi trường (PowerShell):
```powershell
$env:DB_USER="postgres"; $env:DB_PASS="matkhau"
$env:MAIL_USER="email@gmail.com"; $env:MAIL_APP_PASSWORD="app-password-16-ky-tu"
$env:JWT_SECRET="chuoi-bi-mat-dai-tren-32-byte"
$env:CLOUDINARY_CLOUD_NAME="..."; $env:CLOUDINARY_API_KEY="..."; $env:CLOUDINARY_API_SECRET="..."
$env:VNPAY_TMN_CODE="..."; $env:VNPAY_HASH_SECRET="..."
```

**Các khoá cấu hình chuẩn (đã set sẵn trong `src/main/resources/application.yml`):**
```yaml
spring.datasource.url: jdbc:postgresql://localhost:5432/starshop
spring.jpa.hibernate.ddl-auto: none        # FLYWAY là nguồn schema duy nhất
spring.jpa.open-in-view: false             # -> query quan hệ phải dùng @EntityGraph/JOIN FETCH
spring.flyway.enabled: true                # locations: classpath:db/migration
spring.mvc.view.prefix: /WEB-INF/views/    # suffix: .jsp
spring.mail.host: smtp.gmail.com  port: 587 (STARTTLS, App Password)
server.port: 8080
starshop.jwt: { secret: ${JWT_SECRET}, access-token-minutes: 30 }
starshop.otp: { length: 6, ttl-minutes: 5 }
starshop.vnpay: { pay-url: .../vpcpay.html, return-url: http://localhost:8080/checkout/vnpay-return }
```

> ⚠️ **2 điều dễ chết cấu hình:** (1) JSP chỉ chạy khi **war** + `tomcat-embed-jasper` (provided) + jakarta JSTL. (2) Web dùng **session** (form login); **JWT** chỉ cho `/api/**` (JwtAuthenticationFilter) — không ép JWT toàn site.

---

## 3. CẤU HÌNH DỰ ÁN (cấu trúc + nguồn chân lý SSOT)

```
starshop/
├─ config/       SecurityConfig(6 role), SiteMesh, WebSocket, Cloudinary, Jwt   → L
├─ domain/       27 entity JPA + enums                                          → L
├─ repository/   JpaRepository (dùng @EntityGraph chống N+1)                     → L
├─ service/      auth,common,customer(L) · vendor,shipper(TV2) · admin(TV3)
├─ controller/   Home,auth,customer,api(L) · vendor,shipper(TV2) · admin,manager(TV3)
└─ webapp/
   ├─ assets/css/tokens.css   ← SSOT màu/size/spacing/radius (CẤM hex rải rác)
   ├─ WEB-INF/tags/*.tag       ← component dùng chung (icon,status-badge,card,form-field,table,...)
   └─ WEB-INF/views/decorators/default.jsp ← layout SiteMesh chung (nav theo role)
```

**Nguồn chân lý (chỉ định nghĩa 1 nơi):**
| Thứ | Ở đâu |
|---|---|
| Màu/size/spacing/radius | `assets/css/tokens.css` |
| Component UI | `WEB-INF/tags/*.tag` |
| Icon | `icon.tag` (registry Lucide) |
| Schema DB | Flyway `db/migration/V1..` (ddl-auto=none) |
| Trạng thái đơn/SP | enum + `status-badge.tag` |
| Metric (doanh thu, sold_count, rating_avg) | tính 1 chỗ ở service (rules/database/05) |

---

## 4. LUẬT FRONTEND (tóm tắt — chi tiết `rules/frontend/`)

- **Không emoji trong UI.** Icon chỉ dùng **Lucide** (stroke 1.5, currentColor) qua `<t:icon name="...">`.
- **Không hard-code** màu/size → dùng `var(--…)` từ tokens.css.
- Chỉ **4 app shell** (public/vendor/admin/shipper) kế thừa 1 `default.jsp`. Không tự đẻ layout.
- Markup lặp = **JSP tag** (`WEB-INF/tags`), không copy-paste `<table>`/`<form>`.
- **Mỗi màn đủ 4 trạng thái**: loading · empty (có nút) · error (nút thử lại) · success (`<t:empty-state>`).
- **StatusBadge SSOT**: truyền enum thô, không tự đặt màu.
- Typography: body ≥ 14px, caption ≥ 12px. Spacing thang 4px. a11y: skip-link, focus ring, tương phản ≥ 4.5:1, touch ≥ 44px.
- **Cổng chống phình FE**: trước khi tạo trang/component/màu/icon mới → kiểm đã có sẵn chưa (rules/frontend/06).

---

## 5. LUẬT DATABASE (tóm tắt — chi tiết `rules/database/`)

- **Naming**: snake_case tiếng Anh, bảng số nhiều; FK `<đơn>_id`; enum UPPER_SNAKE.
- **Chống đẻ/phình bảng**: thuộc tính lặp nhiều dòng → tách bảng con + FK (vd nhiều địa chỉ → `user_addresses`); đúng 3NF.
- **Datatype**: `bigserial` PK, `timestamptz` thời gian, `numeric(12,2)` tiền, `boolean` cờ, enum = varchar+CHECK.
- **Bảo mật (thay RLS)**: Spring Security phân quyền 6 role + kiểm **ownership** ở Service (chống IDOR); BCrypt; JWT; CSRF cho form.
- **Index**: mọi FK + cột lọc/sắp xếp; partial index cho `status`/`deleted_at`.
- **Media**: ảnh → Cloudinary (DB lưu URL); video → link; **không lưu BLOB**.
- **Metric SSOT**: `order.total = subtotal + shipping_fee − discount`; `rating_avg` = trung bình review; cập nhật 1 chỗ.

---

## 6. CHẠY & TÀI KHOẢN DEMO

```bash
# tạo DB rỗng: CREATE DATABASE starshop;  (PostgreSQL 16)
cd starshop && mvn spring-boot:run        # http://localhost:8080
mvn clean package                          # -> target/starshop.war (Tomcat 10.1)
```
Tài khoản demo (mật khẩu tất cả: `123456`): `user@` · `vendor@` · `manager@` · `admin@` · `shipper@` `starshop.vn`.

---

## 7. CÁCH THỰC THI (khi code)
1. Trước mỗi task FE/DB: mở `CLAUDE.md` → dò router theo từ khoá → đọc đúng file `rules/`.
2. Qua **cổng chống phình** mới được tạo mới.
3. Chỉ sửa file có `@owner` đúng tên mình; không đụng config/domain/tokens/pom (trừ Leader).
4. Xong: chạy checklist `✅ Kiểm` cuối mỗi file luật + tự kiểm §3 CLAUDE.md → mở PR (Leader review).

*Hết — Prompt & Cấu hình chuẩn Floria.*
