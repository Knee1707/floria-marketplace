package com.starshop.controller.customer;

import com.starshop.service.customer.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;

/** Đánh giá + bình luận SP đã mua (F3.17). @owner: L. */
@Controller
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public String add(@RequestParam Long productId, @RequestParam int rating,
                      @RequestParam String comment,
                      @RequestParam(required = false) String mediaUrls,
                      RedirectAttributes ra) {
        List<String> urls = (mediaUrls == null || mediaUrls.isBlank())
                ? List.of() : Arrays.asList(mediaUrls.split("\\s*,\\s*"));
        try {
            reviewService.addReview(productId, rating, comment, urls);
            ra.addFlashAttribute("reviewOk", "Cảm ơn đánh giá của bạn!");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("reviewError", ex.getMessage());
        }
        return "redirect:/products/" + productId;
    }
}
