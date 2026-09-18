package com.starshop.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cấu hình Cloudinary để upload ảnh/video (F7.3).
 * @owner: TV2 (viết CloudinaryService dùng bean này). Leader dựng bean.
 */
@Configuration
public class CloudinaryConfig {

    @Value("${starshop.cloudinary.cloud-name}")
    private String cloudName;
    @Value("${starshop.cloudinary.api-key}")
    private String apiKey;
    @Value("${starshop.cloudinary.api-secret}")
    private String apiSecret;

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true
        ));
    }
}
