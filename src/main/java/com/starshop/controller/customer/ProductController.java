package com.starshop.controller.customer;

import com.starshop.domain.enums.CommonEnums.ProductStatus;
import com.starshop.repository.ProductRepository;
import com.starshop.service.customer.ReviewService;
import com.starshop.service.customer.ViewHistoryService;
import com.starshop.service.customer.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Danh sách + tìm kiếm + lọc + chi tiết sản phẩm (F3.2–F3.7, F3.16, F3.17). @owner: L.
 * Tránh N+1 nhờ @EntityGraph ở repository.
 */
@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductRepository productRepository;
    private final ReviewService reviewService;
    private final ViewHistoryService viewHistoryService;
    private final WishlistService wishlistService;
    private static final int PAGE_SIZE = 20;

    @GetMapping
    public String list(@RequestParam(defaultValue = "") String keyword,
                       @RequestParam(required = false) Long categoryId,
                       @RequestParam(defaultValue = "0") int page,
                       Model model) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), PAGE_SIZE, Sort.by("soldCount").descending());
        Page<?> products = (categoryId != null)
                ? productRepository.findByStatusAndCategoryId(ProductStatus.ACTIVE, categoryId, pageable)
                : productRepository.findByStatusAndNameContainingIgnoreCase(ProductStatus.ACTIVE, keyword, pageable);
        model.addAttribute("products", products);
        model.addAttribute("keyword", keyword);
        model.addAttribute("pageTitle", "Sản phẩm");
        return "customer/product-list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Authentication auth, Model model) {
        var product = productRepository.findWithStoreById(id).orElse(null);
        if (product == null) {
            model.addAttribute("code", 404);
            model.addAttribute("message", "Không tìm thấy sản phẩm.");
            return "error/generic";
        }
        viewHistoryService.record(id);                    // F3.16 ghi "đã xem" (tự bỏ qua nếu chưa đăng nhập)
        model.addAttribute("product", product);
        model.addAttribute("reviews", reviewService.forProduct(id));  // F3.17
        boolean loggedIn = auth != null && auth.isAuthenticated()
                && !"anonymousUser".equals(auth.getPrincipal());
        model.addAttribute("canReview", loggedIn && reviewService.canReview(id));
        model.addAttribute("followed", loggedIn && wishlistService.isFollowed(id));
        return "customer/product-detail";
    }
}
