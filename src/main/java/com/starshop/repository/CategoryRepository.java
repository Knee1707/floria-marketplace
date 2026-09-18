package com.starshop.repository;

import com.starshop.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/** @owner: TV3 dùng. */
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByParentIsNull();
    List<Category> findByParentId(Long parentId);
}
