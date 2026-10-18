package com.starshop.dto.admin;

import com.starshop.domain.enums.CommonEnums.PromotionType;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Form khu quản trị — validate lớp server (@Valid). Lớp client dùng thuộc tính HTML5 tương ứng trong JSP.
 * Gom chung 1 file để không phình DTO. Chỉ dùng cho binding request (không đọc trong JSP).
 * @owner: TV3
 */
public final class AdminForms {
    private AdminForms() {}

    public record CategoryForm(
            Long id,
            @NotBlank(message = "Nhập tên danh mục") @Size(min = 2, max = 120, message = "Tên danh mục 2–120 ký tự") String name,
            Long parentId,
            @Size(max = 500, message = "URL ảnh tối đa 500 ký tự")
            @Pattern(regexp = "^$|^https?://.+", message = "URL ảnh phải bắt đầu bằng http(s)://") String imageUrl) {}

    public record StyleForm(
            Long id,
            @NotBlank(message = "Nhập tên thuộc tính") @Size(max = 80, message = "Tên thuộc tính tối đa 80 ký tự") String name) {}

    public record StyleValueForm(
            @NotNull Long styleId,
            @NotBlank(message = "Nhập giá trị") @Size(max = 80, message = "Giá trị tối đa 80 ký tự") String value) {}

    public record DeliveryForm(
            Long id,
            @NotBlank(message = "Nhập tên nhà vận chuyển") @Size(min = 2, max = 120, message = "Tên 2–120 ký tự") String name,
            @NotNull(message = "Nhập phí ship") @DecimalMin(value = "0", message = "Phí ship không âm")
            @Digits(integer = 10, fraction = 2, message = "Phí ship không hợp lệ") BigDecimal shippingFee) {}

    public record CommissionForm(
            Long id,
            Long storeId,
            @NotNull(message = "Nhập tỉ lệ") @DecimalMin(value = "0", message = "Tỉ lệ không âm")
            @DecimalMax(value = "100", message = "Tỉ lệ tối đa 100%") BigDecimal rate,
            @Size(max = 255, message = "Ghi chú tối đa 255 ký tự") String note) {}

    /** storeId = null → toàn sàn (chỉ Admin); có storeId → voucher của 1 shop (Admin hoặc Manager phụ trách shop). */
    public record VoucherForm(
            Long storeId,
            @NotBlank(message = "Nhập mã voucher")
            @Pattern(regexp = "^[A-Za-z0-9_-]{3,40}$", message = "Mã 3–40 ký tự: chữ, số, - hoặc _") String code,
            @NotNull(message = "Chọn loại giảm") PromotionType type,
            @NotNull(message = "Nhập giá trị giảm") @DecimalMin(value = "0.01", message = "Giá trị giảm phải > 0") @Digits(integer = 10, fraction = 2, message = "Giá trị giảm quá lớn") BigDecimal value,
            @DecimalMin(value = "0", message = "Đơn tối thiểu không âm") @Digits(integer = 10, fraction = 2, message = "Đơn tối thiểu quá lớn") BigDecimal minOrder,
            @DecimalMin(value = "0", message = "Giảm tối đa không âm") @Digits(integer = 10, fraction = 2, message = "Giảm tối đa quá lớn") BigDecimal maxDiscount,
            @Min(value = 1, message = "Số lượt phải ≥ 1") Integer quota,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {}

    /** storeId = null → toàn sàn (chỉ Admin); có storeId → chương trình của 1 shop. */
    public record PromotionForm(
            Long storeId,
            @NotNull(message = "Chọn loại khuyến mãi") PromotionType type,
            @NotNull(message = "Nhập giá trị") @DecimalMin(value = "0.01", message = "Giá trị phải > 0") @Digits(integer = 10, fraction = 2, message = "Giá trị quá lớn") BigDecimal value,
            @Min(value = 1, message = "Số lượt phải ≥ 1") Integer quota,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {}
}
