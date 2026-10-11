package com.starshop.controller.admin;

import com.starshop.domain.enums.CommonEnums.ProductStatus;
import com.starshop.service.admin.ModerationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * F6.4 Kiểm duyệt sản phẩm (cấm / mở lại). Admin: toàn sàn; Manager: SP của shop được giao.
 * @owner: TV3 | @leaf: F6.4 | @status: DONE
 */
@Controller
@RequestMapping({"/admin/products", "/manager/products"})
@RequiredArgsConstructor
public class ProductModerationController {

    private static final int PAGE_SIZE = 12;

    private final ModerationService moderationService;

    @GetMapping
    public String list(@RequestParam(required = false) String keyword,
                       @RequestParam(defaultValue = "0") int page, Model model) {
        var pageable = PageRequest.of(Math.max(page, 0), PAGE_SIZE, Sort.by("id").descending());
        model.addAttribute("products", moderationService.products(keyword, pageable));
        model.addAttribute("keyword", keyword);
        model.addAttribute("keywordParam", AdminWeb.urlParam(keyword));
        return "admin/products";
    }

    @PostMapping("/{id}/status")
    public String setStatus(@PathVariable Long id, @RequestParam ProductStatus status,
                            HttpServletRequest req, RedirectAttributes ra) {
        moderationService.setProductStatus(id, status);
        AdminWeb.ok(ra, status == ProductStatus.BANNED ? "Đã cấm sản phẩm" : "Đã mở lại sản phẩm");
        return "redirect:/" + AdminWeb.area(req) + "/products";
    }
}
