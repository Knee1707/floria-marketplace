package com.starshop.controller.admin;

import com.starshop.domain.enums.Role;
import com.starshop.service.admin.UserAdminService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * F6.1 Quản lý user: tìm kiếm + lọc vai trò + phân trang, khoá/mở.
 * Admin (/admin/users): toàn hệ thống + tạo / xoá / đổi vai trò.
 * Manager (/manager/users): user thuộc shop được giao, chỉ khoá/mở (service kiểm phạm vi).
 * @owner: TV3 | @leaf: F6.1 | @status: DONE
 */
@Controller
@RequestMapping({"/admin/users", "/manager/users"})
@RequiredArgsConstructor
public class UserAdminController {

    private static final int PAGE_SIZE = 10;

    private final UserAdminService userAdminService;

    @GetMapping
    public String list(@RequestParam(required = false) String keyword,
                       @RequestParam(required = false) Role role,
                       @RequestParam(defaultValue = "0") int page, Model model) {
        var pageable = PageRequest.of(Math.max(page, 0), PAGE_SIZE, Sort.by("createdAt").descending().and(Sort.by("id")));
        model.addAttribute("users", userAdminService.search(keyword, role, pageable));
        model.addAttribute("keyword", keyword);
        model.addAttribute("keywordParam", AdminWeb.urlParam(keyword));
        model.addAttribute("role", role);
        model.addAttribute("roles", userAdminService.visibleRoles());
        return "admin/users";
    }

    @PostMapping("/{id}/lock")
    public String lock(@PathVariable Long id, @RequestParam boolean locked,
                       HttpServletRequest req, RedirectAttributes ra) {
        userAdminService.setLocked(id, locked);
        AdminWeb.ok(ra, locked ? "Đã khoá tài khoản" : "Đã mở khoá tài khoản");
        return redirect(req);
    }

    @PostMapping("/{id}/role")
    public String changeRole(@PathVariable Long id, @RequestParam Role role,
                             HttpServletRequest req, RedirectAttributes ra) {
        userAdminService.changeRole(id, role);
        AdminWeb.ok(ra, "Đã đổi vai trò thành " + role.name());
        return redirect(req);
    }

    // F6.1 (thêm): Admin tạo tài khoản mới (vd người bán / shipper / manager)
    @PostMapping("/create")
    public String create(@RequestParam String fullName, @RequestParam String email,
                         @RequestParam(required = false) String phone,
                         @RequestParam String password, @RequestParam Role role,
                         HttpServletRequest req, RedirectAttributes ra) {
        userAdminService.createUser(fullName, email, phone, password, role);
        AdminWeb.ok(ra, "Đã thêm người dùng " + email);
        return redirect(req);
    }

    // F6.1 (xóa): Admin xoá tài khoản
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, HttpServletRequest req, RedirectAttributes ra) {
        userAdminService.deleteUser(id);
        AdminWeb.ok(ra, "Đã xoá người dùng");
        return redirect(req);
    }

    private static String redirect(HttpServletRequest req) {
        return "redirect:/" + AdminWeb.area(req) + "/users";
    }
}
