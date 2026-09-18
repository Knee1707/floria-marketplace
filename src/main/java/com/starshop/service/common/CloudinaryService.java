package com.starshop.service.common;

import org.springframework.web.multipart.MultipartFile;

/**
 * HỢP ĐỒNG — TV2 viết CloudinaryServiceImpl (@owner: TV2 | F7.3).
 * Dùng bean Cloudinary ở CloudinaryConfig. Trả secure_url; KHÔNG lưu file vào server/DB.
 */
public interface CloudinaryService {
    String upload(MultipartFile file);
}
