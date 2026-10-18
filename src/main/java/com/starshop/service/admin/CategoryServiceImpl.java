package com.starshop.service.admin;

import com.starshop.domain.Category;
import com.starshop.repository.AdminQueryRepository;
import com.starshop.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * F6.2 CRUD danh mục (phân cấp 2 tầng qua parent_id). Admin + Manager (đề: cả 2 role "quản lý danh mục").
 * Danh mục dùng chung cả chuỗi; xoá được chặn khi còn danh mục con / sản phẩm đang dùng.
 * @owner: TV3 | @leaf: F6.2 | @status: DONE
 */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final AdminQueryRepository adminQueryRepository;
    private final AdminScope adminScope;

    /** Danh sách kèm danh mục cha (JOIN FETCH), cha đứng trước các con. */
    @Override
    @Transactional(readOnly = true)
    public List<Category> all() {
        return adminQueryRepository.findAllCategoriesWithParent();
    }

    @Override
    @Transactional
    public void save(Long id, String name, Long parentId, String imageUrl) {
        adminScope.requireStaff();
        String cleanName = name == null ? "" : name.trim();
        if (cleanName.length() < 2 || cleanName.length() > 120) {
            throw new IllegalArgumentException("Tên danh mục phải từ 2 đến 120 ký tự");
        }
        Category c = (id == null) ? new Category()
                : categoryRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy danh mục #" + id));

        Category parent = null;
        if (parentId != null) {
            if (parentId.equals(id)) {
                throw new IllegalArgumentException("Danh mục không thể là cha của chính nó");
            }
            parent = categoryRepository.findById(parentId)
                    .orElseThrow(() -> new IllegalArgumentException("Danh mục cha không tồn tại"));
            if (parent.getParent() != null) {
                throw new IllegalArgumentException("Chỉ hỗ trợ 2 cấp: danh mục cha phải là danh mục gốc");
            }
            if (id != null && !categoryRepository.findByParentId(id).isEmpty()) {
                throw new IllegalArgumentException("Danh mục đang có danh mục con nên không thể chuyển thành danh mục con");
            }
        }
        c.setName(cleanName);
        c.setParent(parent);
        c.setImageUrl(imageUrl == null || imageUrl.isBlank() ? null : imageUrl.trim());
        categoryRepository.save(c);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        adminScope.requireStaff();
        if (!categoryRepository.existsById(id)) {
            throw new IllegalArgumentException("Không tìm thấy danh mục #" + id);
        }
        if (!categoryRepository.findByParentId(id).isEmpty()) {
            throw new IllegalArgumentException("Hãy xoá hoặc chuyển các danh mục con trước");
        }
        long used = adminQueryRepository.countProductsByCategory(id);
        if (used > 0) {
            throw new IllegalArgumentException("Danh mục đang có " + used + " sản phẩm, không thể xoá");
        }
        categoryRepository.deleteById(id);
    }
}
