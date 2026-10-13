# PROMPTS — Câu lệnh sẵn dùng cho Floria

> Copy nguyên khối, thay phần trong `{...}`, dán cho AI khi code. Tất cả đều buộc AI theo luật trong repo (`CLAUDE.md` + `rules/`).

---

## 0. Prompt KHỞI ĐỘNG phiên (dán đầu mỗi lần code)
```
Đây là repo Floria (Spring Boot + JSP + JPA + PostgreSQL). Trước khi làm gì, đọc CLAUDE.md,
PROMPT_CAU_HINH_CHUAN.md và các file trong rules/. Tuân thủ tuyệt đối:
- Reuse > Create, Token > Hard-code, MECE (1 nguồn chân lý).
- Không emoji; icon dùng <t:icon>; màu/size dùng var(--...) trong tokens.css.
- Kiến trúc 3 tầng: Controller -> Service -> Repository. Controller không gọi Repository.
- open-in-view=false: query có quan hệ phải dùng @EntityGraph.
- Chỉ sửa file có @owner đúng vai của tôi. Trả lời ngắn gọn, code sạch.
Xác nhận đã đọc luật rồi mới bắt đầu.
```

---

## 1. Prompt LÀM 1 CHỨC NĂNG (chung)
```
Viết chức năng: {mô tả, vd "Vendor cập nhật trạng thái đơn hàng"}.
Yêu cầu:
- Đúng 3 tầng: interface Service (nếu có sẵn thì viết Impl) + Controller + JSP.
- Validation (@Valid) + kiểm quyền sở hữu (chống IDOR).
- JSP dùng component có sẵn (<t:page-header>, <t:card>, <t:data-table>/.ss-table,
  <t:status-badge>, <t:form-field>, <t:pagination>, <t:empty-state>) và đủ 4 trạng thái màn.
- Không hard-code màu/size, không emoji.
- Cần thêm query repository hay đổi entity -> nói rõ cho tôi (Leader) duyệt, ĐỪNG tự sửa entity/config.
Liệt kê các file sẽ tạo/sửa trước, rồi viết code.
```

## 2. Prompt cho TV2 (Vendor / Shipper)
```
Tôi là TV2. Đọc HUONG_DAN_03_TV2_vendor_shipper.md. Làm leaf: {F4.x / F5.x / F7.x}.
Chỉ sửa file @owner:TV2 trong service/vendor, service/shipper, service/common,
controller/vendor, controller/shipper, views/vendor, views/shipper.
Viết Impl cho interface Leader đã khai báo (đừng đổi chữ ký). Kiểm ownership shop/shipper của mình.
Ảnh upload qua CloudinaryService; thông báo realtime qua NotificationService.
```

## 3. Prompt cho TV3 (Manager / Admin / UI)
```
Tôi là TV3. Đọc HUONG_DAN_04_TV3_admin_manager_ui.md. Làm leaf: {F6.x}.
Chỉ sửa file @owner:TV3 trong service/admin, controller/admin, controller/manager,
views/admin, views/manager. Viết Impl cho interface có sẵn.
Admin = toàn hệ thống; Manager = shop được phân công (store_managers).
Dashboard dùng <t:stat-tile> + Chart.js. Thao tác nhạy cảm confirm bằng <t:modal>.
```

## 4. Prompt TẠO TRANG JSP mới
```
Tạo trang JSP {tên} cho {mục đích}. Bắt buộc:
- Khai báo taglib: <%@ taglib prefix="t" tagdir="/WEB-INF/tags" %> (+ c/fmt nếu cần).
- Nằm trong 1 app shell có sẵn (SiteMesh tự bọc), chỉ viết phần nội dung.
- Dùng component chung, KHÔNG tự viết <table>/<form> thô, KHÔNG mã hex/size cứng, KHÔNG emoji.
- Có đủ trạng thái loading/empty/error (dùng <t:empty-state>).
- Form POST có CSRF: <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>.
```

## 5. Prompt VIẾT MIGRATION mới
```
Tạo migration Flyway MỚI (không sửa V1/V2/V3 đã chạy). Tên: V{n}__{mo_ta}.sql.
Theo luật rules/database: snake_case tiếng Anh, PK bigserial, FK <đơn>_id, timestamptz,
numeric cho tiền, CHECK cho enum, index cho FK + cột lọc. Cập nhật ERD nếu thêm bảng.
Nội dung cần: {mô tả bảng/cột}.
```

## 6. Prompt REVIEW code (chống phình / design-con)
```
Review đoạn code/PR sau theo luật Floria. Chỉ ra vi phạm dạng bảng:
Vi phạm | File | Luật bị phạm (§ trong rules/) | Cách sửa.
Bắt các lỗi: mã hex/size hard-code (phải qua token), emoji trong UI, markup table/form
copy-paste (phải dùng tag), Controller gọi Repository trực tiếp, thiếu @EntityGraph gây N+1,
thiếu kiểm ownership (IDOR), màn thiếu trạng thái loading/empty/error, đẻ bảng/cột thừa (không 3NF).
{dán code}
```

## 7. Prompt TỐI ƯU QUERY (postgresql-optimize)
```
Tối ưu truy vấn/endpoint sau. Quy trình: đo (EXPLAIN ANALYZE) -> chẩn đoán -> sửa 1 thứ -> đo lại.
Ưu tiên: đúng index (FK + cột lọc, partial/GIN), tránh N+1 bằng @EntityGraph/JOIN FETCH,
keyset pagination nếu bảng lớn, DTO projection thay vì nạp cả entity. Giải thích vì sao.
{dán query / repository / mô tả}
```

## 8. Prompt CHỐT CHUẨN NGÀNH (chuan-nganh)
```
Kiểm "chuẩn ngành" cho quyết định: {vấn đề}. Bối cảnh: đồ án Floria, quy mô nhỏ, 1 server.
Thu >=3 nguồn uy tín thật (docs chính thức + công ty đầu ngành + chuyên gia), đối chiếu có số liệu,
rồi đề xuất phương án hợp bối cảnh của tôi. Không bịa nguồn; ghi link.
```

## 9. Prompt SỬA LỖI BUILD
```
Tôi chạy `mvn spring-boot:run` và gặp lỗi sau. Đọc log, tìm nguyên nhân gốc, sửa tối thiểu,
giữ đúng luật (3 tầng, không hard-code, @EntityGraph cho quan hệ). Liệt kê file sửa + lý do.
{dán log lỗi}
```

## 10. Prompt COMMIT / PR (đúng HUONG_DAN_05)
```
Tạo commit theo Conventional Commits cho thay đổi vừa rồi: type(scope): mô tả ngắn tiếng Việt.
Đề xuất tên nhánh feature/{tv}-{leaf} và nội dung mô tả PR (việc đã làm + cách test).
```

---

> Mẹo: luôn dán **Prompt 0** trước, rồi mới dán prompt việc cụ thể. Muốn ép AI đọc đúng luật của chủ đề,
> nhắc từ khoá để nó tự dò router trong CLAUDE.md (vd "icon", "màu", "bảng mới", "thống kê"...).
```
