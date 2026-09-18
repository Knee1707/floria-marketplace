package com.starshop.service.admin;

import com.starshop.domain.Category;
import java.util.List;

/** HỢP ĐỒNG — TV3 viết CategoryServiceImpl (@owner: TV3 | F6.2). */
public interface CategoryService {
    List<Category> all();
    void save(Long id, String name, Long parentId, String imageUrl); // id null = tạo mới
    void delete(Long id);
}
