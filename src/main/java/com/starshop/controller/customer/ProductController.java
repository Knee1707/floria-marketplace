package com.starshop.controller.customer;

import com.starshop.domain.Product;
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

import java.math.BigDecimal;

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
                       @RequestParam(required = false) String categoryId,
                       @RequestParam(defaultValue = "banchay") String sort,
                       @RequestParam(required = false) String minPrice,
                       @RequestParam(required = false) String maxPrice,
                       @RequestParam(defaultValue = "0") int page,
                       Model model) {
        // 4 tiêu chí sắp xếp theo đề: mới / bán chạy / đánh giá / yêu thích (giảm dần).
        Sort order = switch (sort) {
            case "moi"      -> Sort.by("createdAt").descending();
            case "danhgia"  -> Sort.by("ratingAvg").descending();
            case "yeuthich" -> Sort.by("favoriteCount").descending();
            default         -> Sort.by("soldCount").descending();   // banchay (mặc định)
        };
        Pageable pageable = PageRequest.of(Math.max(page, 0), PAGE_SIZE, order);

        Page<Product> products = productRepository.search(
                ProductStatus.ACTIVE, parseId(categoryId),
                keyword == null ? "" : keyword.trim(),
                parsePrice(minPrice), parsePrice(maxPrice), pageable);

        model.addAttribute("products", products);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sort", sort);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("pageTitle", "Sản phẩm");
        return "customer/product-list";
    }

    /** Parse an toàn khoảng giá từ query (rỗng/sai -> null = không lọc). */
    private BigDecimal parsePrice(String s) {
        try {
            return (s == null || s.isBlank()) ? null : new BigDecimal(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** Parse an toàn id danh mục từ query (rỗng/sai -> null = mọi danh mục). */
    private Long parseId(String s) {
        try {
            return (s == null || s.isBlank()) ? null : Long.valueOf(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
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
