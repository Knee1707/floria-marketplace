package com.starshop.controller.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starshop.dto.admin.DashboardData;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

/**
 * Tiện ích web dùng chung cho các controller Admin/Manager (không chứa nghiệp vụ).
 * Một controller có thể phục vụ cả /admin/** và /manager/** — "area" cho biết đang ở khu nào
 * để view vẽ đúng menu và redirect đúng chỗ. Phân quyền URL vẫn do SecurityConfig (Leader).
 * @owner: TV3
 */
public final class AdminWeb {
    private AdminWeb() {}

    public static final String AREA_ADMIN = "admin";
    public static final String AREA_MANAGER = "manager";

    public static String area(HttpServletRequest req) {
        return req.getServletPath().startsWith("/" + AREA_MANAGER) ? AREA_MANAGER : AREA_ADMIN;
    }

    /** Lỗi validate @Valid → IllegalArgumentException (AdminAreaAdvice đổi thành thông báo lỗi). */
    public static void requireValid(BindingResult br) {
        if (br.hasErrors()) {
            String msg = br.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .distinct()
                    .collect(Collectors.joining(" · "));
            throw new IllegalArgumentException(msg);
        }
    }

    public static void ok(RedirectAttributes ra, String message) {
        ra.addFlashAttribute("toast", message);
    }

    /** Đưa DTO dashboard + JSON biểu đồ vào model (dùng chung /admin và /manager). */
    public static void dashboardModel(DashboardData data, ObjectMapper mapper, Model model) {
        model.addAttribute("dash", data);
        try {
            // Nhúng JSON vào <script type="application/json">: thoát "<" để không thể đóng thẻ script (chống XSS).
            model.addAttribute("chartJson", mapper.writeValueAsString(data.getChart()).replace("<", "\\u003c"));
        } catch (JsonProcessingException e) {
            model.addAttribute("chartJson", "null");
        }
    }

    public static String urlParam(String s) {
        return s == null ? "" : URLEncoder.encode(s, StandardCharsets.UTF_8);
    }
}
