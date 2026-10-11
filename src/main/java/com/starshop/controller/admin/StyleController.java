package com.starshop.controller.admin;

import com.starshop.dto.admin.AdminForms.StyleForm;
import com.starshop.dto.admin.AdminForms.StyleValueForm;
import com.starshop.service.admin.StyleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * F6.3 Thuộc tính SP: master (Style) – detail (StyleValue). Chỉ Admin.
 * @owner: TV3 | @leaf: F6.3 | @status: DONE
 */
@Controller("adminStyleController") // tên bean riêng, tránh trùng nếu nơi khác có lớp cùng tên
@RequestMapping("/admin/styles")
@RequiredArgsConstructor
public class StyleController {

    private final StyleService styleService;

    @GetMapping
    public String list(@RequestParam(required = false) Long styleId, Model model) {
        var styles = styleService.allStyles();
        model.addAttribute("styles", styles);
        // Mặc định mở thuộc tính đầu tiên để màn phải không trống.
        Long selected = (styleId == null && !styles.isEmpty()) ? styles.get(0).getId() : styleId;
        model.addAttribute("selectedId", selected);
        model.addAttribute("values", selected == null ? java.util.List.of() : styleService.valuesOf(selected));
        return "admin/styles";
    }

    @PostMapping
    public String saveStyle(@Valid @ModelAttribute StyleForm form, BindingResult br, RedirectAttributes ra) {
        AdminWeb.requireValid(br);
        styleService.saveStyle(form.id(), form.name());
        AdminWeb.ok(ra, form.id() == null ? "Đã thêm thuộc tính" : "Đã đổi tên thuộc tính");
        return form.id() == null ? "redirect:/admin/styles" : "redirect:/admin/styles?styleId=" + form.id();
    }

    @PostMapping("/values")
    public String addValue(@Valid @ModelAttribute StyleValueForm form, BindingResult br, RedirectAttributes ra) {
        AdminWeb.requireValid(br);
        styleService.addValue(form.styleId(), form.value());
        AdminWeb.ok(ra, "Đã thêm giá trị");
        return "redirect:/admin/styles?styleId=" + form.styleId();
    }

    @PostMapping("/values/{id}/delete")
    public String deleteValue(@PathVariable Long id, @RequestParam Long styleId, RedirectAttributes ra) {
        styleService.deleteValue(id);
        AdminWeb.ok(ra, "Đã xoá giá trị");
        return "redirect:/admin/styles?styleId=" + styleId;
    }
}
