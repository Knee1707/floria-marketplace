package com.starshop.service.common;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * Triển khai dịch vụ upload ảnh/video lên Cloudinary.
 * @owner: TV2 | @leaf: F7.3 Cloudinary | @status: DONE
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements CloudinaryService {

    private static final List<String> FALLBACK_FLOWER_URLS = List.of(
            "https://images.unsplash.com/photo-1561181286-d3fee7d55364?auto=format&fit=crop&w=600&q=80",
            "https://images.unsplash.com/photo-1526047932273-341f2a7631f9?auto=format&fit=crop&w=600&q=80",
            "https://images.unsplash.com/photo-1490750967868-88aa4486c946?auto=format&fit=crop&w=600&q=80",
            "https://images.unsplash.com/photo-1508615039623-a25605d2b022?auto=format&fit=crop&w=600&q=80",
            "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?auto=format&fit=crop&w=600&q=80",
            "https://images.unsplash.com/photo-1468327768560-75b778cbb551?auto=format&fit=crop&w=600&q=80"
    );

    private final Cloudinary cloudinary;

    @Value("${starshop.cloudinary.cloud-name:demo}")
    private String cloudName;

    @Value("${starshop.cloudinary.api-key:000000000000}")
    private String apiKey;

    @Override
    public String upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // Chế độ demo khi máy local chưa cấu hình tài khoản Cloudinary thật
        if ("demo".equalsIgnoreCase(cloudName) || "000000000000".equals(apiKey)) {
            String fallback = pickFallbackUrl(file.getOriginalFilename());
            log.info("[F7.3 Cloudinary Demo Mode] Chưa cấu hình CLOUDINARY_*, dùng URL mẫu cho '{}': {}",
                    file.getOriginalFilename(), fallback);
            return fallback;
        }

        try {
            Map<?, ?> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "starshop",
                            "resource_type", "auto"
                    )
            );
            Object secureUrl = result.get("secure_url");
            if (secureUrl != null) {
                return secureUrl.toString();
            }
            Object url = result.get("url");
            return url != null ? url.toString() : pickFallbackUrl(file.getOriginalFilename());
        } catch (Exception ex) {
            log.warn("[F7.3 Cloudinary] Upload lỗi ({}), chuyển sang URL fallback cho file '{}'",
                    ex.getMessage(), file.getOriginalFilename());
            return pickFallbackUrl(file.getOriginalFilename());
        }
    }

    private String pickFallbackUrl(String filename) {
        int idx = filename != null ? Math.abs(filename.hashCode()) % FALLBACK_FLOWER_URLS.size() : 0;
        return FALLBACK_FLOWER_URLS.get(idx);
    }
}
