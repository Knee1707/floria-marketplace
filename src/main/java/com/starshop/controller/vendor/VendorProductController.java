package com.starshop.controller.vendor;

import com.starshop.domain.Product;
import com.starshop.domain.Store;
import com.starshop.service.common.CloudinaryService;
import com.starshop.service.vendor.StoreService;
import com.starshop.service.vendor.VendorProductService;
import com.starshop.service.vendor.VendorStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

/**
 * Controller quản lý Sản phẩm của cửa hàng (F4.3 CRUD sản phẩm + upload ảnh Cloudinary).
 * @owner: TV2 | @leaf: F4.3 CRUD sản phẩm | @status: DONE
 */
@Controller
@RequestMapping("/vendor/products")
@RequiredArgsConstructor
public class VendorProductController {

    private final VendorProductService vendorProductService;
    private final StoreService storeService;
    private final VendorStatsService vendorStatsService;
    private final CloudinaryService cloudinaryService;

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page, Model model) {
        Store store = storeService.myStore();
        if (store == null) {
            return "redirect:/vendor/register";
        }
        Page<Product> productPage = vendorProductService.myProducts(PageRequest.of(Math.max(page, 0), 10));
        model.addAttribute("store", store);
        model.addAttribute("page", productPage);
        return "vendor/products";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        Store store = storeService.myStore();
        if (store == null) {
            return "redirect:/vendor/register";
        }
        model.addAttribute("store", store);
        model.addAttribute("categories", vendorStatsService.listCategories());
        return "vendor/product-form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes ra) {
        Store store = storeService.myStore();
        if (store == null) {
            return "redirect:/vendor/register";
        }
        try {
            Product product = vendorProductService.get(id);
            model.addAttribute("store", store);
            model.addAttribute("product", product);
            model.addAttribute("categories", vendorStatsService.listCategories());
            return "vendor/product-form";
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("toastError", ex.getMessage());
            return "redirect:/vendor/products";
        }
    }

    @PostMapping("/save")
    public String save(@RequestParam(required = false) Long id,
                       @RequestParam String name,
                       @RequestParam(required = false) String description,
                       @RequestParam BigDecimal price,
                       @RequestParam Integer stock,
                       @RequestParam(required = false) Long categoryId,
                       @RequestParam(required = false) String thumbnailUrl,
                       @RequestParam(required = false) MultipartFile imageFile,
                       RedirectAttributes ra) {
        try {
            String finalThumb = thumbnailUrl;
            if (imageFile != null && !imageFile.isEmpty()) {
                String uploaded = cloudinaryService.upload(imageFile);
                if (uploaded != null) {
                    finalThumb = uploaded;
                }
            }
            vendorProductService.save(id, name, description, price, stock, categoryId, finalThumb);
            ra.addFlashAttribute("toast", id == null ? "Đã thêm sản phẩm mới thành công." : "Đã cập nhật sản phẩm thành công.");
            return "redirect:/vendor/products";
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("toastError", ex.getMessage());
            return id == null ? "redirect:/vendor/products/new" : "redirect:/vendor/products/" + id + "/edit";
        }
    }

    @PostMapping("/{id}/hide")
    public String toggleHide(@PathVariable Long id, RedirectAttributes ra) {
        try {
            vendorProductService.hide(id);
            ra.addFlashAttribute("toast", "Đã cập nhật trạng thái hiển thị của sản phẩm.");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("toastError", ex.getMessage());
        }
        return "redirect:/vendor/products";
    }
}
