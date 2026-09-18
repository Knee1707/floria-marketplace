package com.starshop.controller.customer;

import com.starshop.service.customer.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/** Thích sản phẩm — wishlist (F3.15). @owner: L. */
@Controller
@RequestMapping("/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping
    public String view(Model model) {
        model.addAttribute("items", wishlistService.myWishlist());
        return "customer/wishlist";
    }

    @PostMapping("/toggle")
    public String toggle(@RequestParam Long productId,
                         @RequestParam(defaultValue = "/wishlist") String redirect) {
        wishlistService.toggle(productId);
        return "redirect:" + redirect;
    }
}
