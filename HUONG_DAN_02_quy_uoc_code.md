# HƯỚNG DẪN 02 — Quy ước code (đọc kỹ trước khi viết)

> Mục tiêu: 3 người code như một. Vi phạm → PR bị trả lại.

## 1. Kiến trúc 3 tầng (BẮT BUỘC — barem 0.5đ)
```
Controller  → nhận request, gọi Service, trả view/JSP.  KHÔNG chứa logic, KHÔNG gọi Repository.
Service     → toàn bộ logic + validation + @Transactional. Trả DTO/entity.
Repository  → chỉ truy vấn DB (JpaRepository / @Query).
```
Leader đã khai báo sẵn **interface Service** (hợp đồng) trong `service/<domain>/`. Bạn **viết class Impl** (`...ServiceImpl`) — KHÔNG đổi chữ ký interface (đổi thì báo Leader).

## 2. Nhãn @owner (mỗi stub có sẵn)
```java
// @owner: TV2 | @leaf: F4.3 CRUD sản phẩm | @status: TODO
```
- Chỉ sửa file có `@owner` đúng tên mình. Xong đổi `@status: DONE`.
- KHÔNG sửa: `config/`, `domain/` (entity), `default.jsp`, `pom.xml`, `tokens.css`, Flyway V1/V2. Cần đổi → mở issue cho Leader.

## 3. Đặt tên (chi tiết ở rules/database/01-naming.md & PHAN_CONG §4)
- DB: snake_case tiếng Anh, bảng số nhiều. Java: Entity PascalCase số ít, field camelCase.
- Controller: `<Domain>Controller`; Service: `XxxService`/`XxxServiceImpl`; Repository: `XxxRepository`.
- JSP: kebab/camel (`product-list.jsp`).

## 4. Cách viết 1 Controller (mẫu)
```java
@Controller @RequestMapping("/vendor/products") @RequiredArgsConstructor
public class VendorProductController {
    private final VendorProductService service;   // gọi service, KHÔNG gọi repo
    @GetMapping public String list(Model model){ model.addAttribute("products", service.listMyProducts()); return "vendor/products"; }
}
```

## 5. Cách viết 1 Service (interface có sẵn → viết Impl)
```java
@Service @RequiredArgsConstructor
public class VendorProductServiceImpl implements VendorProductService {
    private final ProductRepository productRepo;
    @Override @Transactional
    public void create(ProductForm f){ /* validate + map + save */ }
}
```

## 6. Cách viết 1 trang JSP (BẮT BUỘC dùng design system)
- Khai báo: `<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>` + `c`/`fmt` khi cần.
- Dùng component có sẵn: `<t:page-header>`, `<t:card>`, `<t:data-table>` (hoặc `.ss-table`), `<t:status-badge>`, `<t:form-field>`, `<t:pagination>`, `<t:empty-state>`, `<t:icon>`, `<t:stat-tile>`, `<t:modal>`.
- **Mọi màn danh sách phải có đủ trạng thái** loading/empty/error (dùng `<t:empty-state>`).
- **Cấm**: emoji, mã hex/size viết cứng (dùng `var(--…)`), copy-paste `<table>`/`<form>` (dùng tag).
- Icon: `<t:icon name="cart"/>` (registry Lucide) — không tự chèn SVG lung tung.

## 7. Chống N+1 (khi query có quan hệ)
Nếu JSP đọc `product.store.name`… mà `open-in-view=false` → thêm `@EntityGraph(attributePaths={"store"})` vào method repository (nhờ Leader nếu cần sửa repo).

## 8. Bảo mật (barem nặng)
- Kiểm quyền sở hữu ở Service: user/vendor/shipper chỉ thao tác dữ liệu của mình (chống IDOR).
- Form POST luôn có CSRF token: `<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>`.
- Validate cả client (Bootstrap) + server (`@Valid`).

## 9. Checklist "Done" (Leader duyệt)
Đúng 3 tầng · validation + security · JSP dùng tag + đủ trạng thái · không hard-code/emoji · có seed demo · PR sạch.
