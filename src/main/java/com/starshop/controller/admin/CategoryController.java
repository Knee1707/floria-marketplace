package com.starshop.controller.admin;

import com.starshop.dto.admin.AdminForms.CategoryForm;
import com.starshop.service.admin.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * F6.2 CRUD danh mục hoa (2 cấp). Chỉ Admin.
 * @owner: TV3 | @leaf: F6.2 | @status: DONE
 */
@Controller("adminCategoryController") // tên bean riêng, tránh trùng nếu nơi khác có lớp cùng tên
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("categories", categoryService.all());
        return "admin/categories";
    }

    @PostMapping
    public String save(@Valid @ModelAttribute CategoryForm form, BindingResult br, RedirectAttributes ra) {
        AdminWeb.requireValid(br);
        categoryService.save(form.id(), form.name(), form.parentId(), form.imageUrl());
        AdminWeb.ok(ra, form.id() == null ? "Đã thêm danh mục" : "Đã cập nhật danh mục");
        return "redirect:/admin/categories";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        categoryService.delete(id);
        AdminWeb.ok(ra, "Đã xoá danh mục");
        return "redirect:/admin/categories";
    }
}
