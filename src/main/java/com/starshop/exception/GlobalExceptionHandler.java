package com.starshop.exception;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/** Bắt lỗi toàn cục -> render trang lỗi thân thiện. @owner: TV1 (khung dùng chung). */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDenied(Model model) {
        model.addAttribute("code", 403);
        model.addAttribute("message", "Bạn không có quyền truy cập trang này.");
        return "error/generic";
    }

    @ExceptionHandler(Exception.class)
    public String handleAll(Exception ex, Model model) {
        model.addAttribute("code", 500);
        model.addAttribute("message", "Đã có lỗi xảy ra: " + ex.getMessage());
        return "error/generic";
    }
}
