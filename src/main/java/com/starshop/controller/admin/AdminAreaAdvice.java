package com.starshop.controller.admin;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.ui.Model;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * Áp cho controller khu quản trị (admin + manager):
 *  - Đưa "area" (admin|manager) vào model cho shell/menu.
 *  - Lỗi nghiệp vụ/validate (IllegalArgumentException) hoặc tham số sai kiểu/thiếu của thao tác POST → quay lại trang danh sách
 *    kèm thông báo lỗi (toast), thay vì văng trang 500.
 * AccessDeniedException KHÔNG xử lý ở đây → GlobalExceptionHandler (403) của Leader.
 * @Order cao nhất để được xét trước handler Exception chung.
 * @owner: TV3
 */
@ControllerAdvice(basePackages = {"com.starshop.controller.admin", "com.starshop.controller.manager"})
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AdminAreaAdvice {

    @ModelAttribute("area")
    public String area(HttpServletRequest req) {
        return AdminWeb.area(req);
    }

    @ExceptionHandler({IllegalArgumentException.class,
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class})
    public String handleBusinessError(Exception ex, HttpServletRequest req, Model model) {
        if (!"POST".equalsIgnoreCase(req.getMethod())) {
            model.addAttribute("code", 400);
            model.addAttribute("message", (ex instanceof IllegalArgumentException) ? ex.getMessage() : "Tham số không hợp lệ");
            return "error/generic";
        }
        String msg = (ex instanceof IllegalArgumentException) ? ex.getMessage() : "Dữ liệu gửi lên không hợp lệ";
        RequestContextUtils.getOutputFlashMap(req).put("toastError", msg);
        String back = sectionPath(req.getServletPath());
        // Trang thuộc tính SP: quay lại đúng thuộc tính đang sửa.
        String styleId = req.getParameter("styleId");
        if (styleId != null && styleId.matches("\\d{1,18}")) back += "?styleId=" + styleId;
        return "redirect:" + back;
    }

    /** "/admin/users/5/lock" → "/admin/users"; "/admin" → "/admin". */
    static String sectionPath(String servletPath) {
        String[] parts = servletPath.split("/");
        // parts[0] = "" ; parts[1] = admin|manager ; parts[2] = section
        if (parts.length >= 3) return "/" + parts[1] + "/" + parts[2];
        return "/" + (parts.length >= 2 ? parts[1] : AdminWeb.AREA_ADMIN);
    }
}
