package com.starshop.controller;

import com.starshop.domain.enums.CommonEnums.ProductStatus;
import com.starshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Trang chủ Guest (F3.1): >10 SP các shop, sắp xếp bán chạy giảm dần.
 * @owner: TV1 — đây là ví dụ 1 slice HOÀN CHỈNH để cả nhóm tham chiếu.
 */
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ProductRepository productRepository;

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        model.addAttribute("topProducts",
                productRepository.findTop12ByStatusOrderBySoldCountDesc(ProductStatus.ACTIVE));
        model.addAttribute("pageTitle", "StarShop - Hoa tươi mỗi ngày");
        return "customer/home"; // -> /WEB-INF/views/customer/home.jsp
    }
}
