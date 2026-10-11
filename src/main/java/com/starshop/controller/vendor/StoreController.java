package com.starshop.controller.vendor;

import com.starshop.domain.Store;
import com.starshop.service.common.CloudinaryService;
import com.starshop.service.vendor.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller xử lý Đăng ký mở cửa hàng (F4.1) và cập nhật hồ sơ cửa hàng (F4.2).
 * Hỗ trợ đồng thời /account/register-store (cho USER chưa lên VENDOR) và /vendor/register.
 * @owner: TV2 | @leaf: F4.1 Đăng ký shop, F4.2 Trang chủ shop | @status: DONE
 */
@Controller
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;
    private final CloudinaryService cloudinaryService;

    // @owner: TV2 | @leaf: F4.1 Đăng ký shop | @status: DONE
    @GetMapping({"/vendor/register", "/account/register-store"})
    public String registerForm(Model model) {
        Store existing = storeService.myStore();
        if (existing != null) {
            return "redirect:/vendor";
        }
        return "vendor/register-store";
    }

    // @owner: TV2 | @leaf: F4.1 Đăng ký shop | @status: DONE
    @PostMapping({"/vendor/register", "/account/register-store"})
    public String registerSubmit(@RequestParam String name,
                                 @RequestParam(required = false) String description,
                                 RedirectAttributes ra) {
        try {
            Store created = storeService.registerStore(name, description);
            ra.addFlashAttribute("toast", "Đăng ký cửa hàng \"" + created.getName() + "\" thành công!");
            return "redirect:/vendor";
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("toastError", ex.getMessage());
            return "redirect:/account/register-store";
        }
    }

    // @owner: TV2 | @leaf: F4.2 Cập nhật thông tin/ảnh shop | @status: DONE
    @PostMapping("/vendor/store/update")
    public String updateStore(@RequestParam Long storeId,
                              @RequestParam String name,
                              @RequestParam(required = false) String description,
                              @RequestParam(required = false) String avatarUrl,
                              @RequestParam(required = false) String coverUrl,
                              @RequestParam(required = false) MultipartFile avatarFile,
                              @RequestParam(required = false) MultipartFile coverFile,
                              RedirectAttributes ra) {
        try {
            String finalAvatar = avatarUrl;
            if (avatarFile != null && !avatarFile.isEmpty()) {
                String uploadedAvatar = cloudinaryService.upload(avatarFile);
                if (uploadedAvatar != null) {
                    finalAvatar = uploadedAvatar;
                }
            }
            String finalCover = coverUrl;
            if (coverFile != null && !coverFile.isEmpty()) {
                String uploadedCover = cloudinaryService.upload(coverFile);
                if (uploadedCover != null) {
                    finalCover = uploadedCover;
                }
            }

            storeService.updateStore(storeId, name, description, finalAvatar, finalCover);
            ra.addFlashAttribute("toast", "Đã cập nhật thông tin cửa hàng thành công.");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("toastError", ex.getMessage());
        }
        return "redirect:/vendor";
    }
}
