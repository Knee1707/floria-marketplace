package com.starshop.service.admin;

import com.starshop.domain.Style;
import com.starshop.domain.StyleValue;
import com.starshop.repository.StyleRepository;
import com.starshop.repository.StyleValueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * F6.3 Thuộc tính sản phẩm: Style (vd "Màu sắc") + StyleValue (vd "Đỏ"). Chỉ Admin sửa.
 * @owner: TV3 | @leaf: F6.3 | @status: DONE
 */
@Service
@RequiredArgsConstructor
public class StyleServiceImpl implements StyleService {

    private final StyleRepository styleRepository;
    private final StyleValueRepository styleValueRepository;
    private final AdminScope adminScope;

    @Override
    @Transactional(readOnly = true)
    public List<Style> allStyles() {
        return styleRepository.findAll(Sort.by("id"));
    }

    @Override
    @Transactional
    public void saveStyle(Long id, String name) {
        adminScope.requireAdmin();
        String clean = requireText(name, 80, "Tên thuộc tính");
        Style s = (id == null) ? new Style()
                : styleRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thuộc tính #" + id));
        s.setName(clean);
        styleRepository.save(s);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StyleValue> valuesOf(Long styleId) {
        return styleValueRepository.findByStyleId(styleId);
    }

    @Override
    @Transactional
    public void addValue(Long styleId, String value) {
        adminScope.requireAdmin();
        String clean = requireText(value, 80, "Giá trị");
        Style style = styleRepository.findById(styleId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thuộc tính #" + styleId));
        boolean duplicated = styleValueRepository.findByStyleId(styleId).stream()
                .anyMatch(v -> v.getValue().equalsIgnoreCase(clean));
        if (duplicated) {
            throw new IllegalArgumentException("Giá trị \"" + clean + "\" đã tồn tại");
        }
        styleValueRepository.save(StyleValue.builder().style(style).value(clean).build());
    }

    @Override
    @Transactional
    public void deleteValue(Long valueId) {
        adminScope.requireAdmin();
        if (!styleValueRepository.existsById(valueId)) {
            throw new IllegalArgumentException("Không tìm thấy giá trị #" + valueId);
        }
        styleValueRepository.deleteById(valueId);
    }

    private static String requireText(String s, int max, String field) {
        String clean = s == null ? "" : s.trim();
        if (clean.isEmpty() || clean.length() > max) {
            throw new IllegalArgumentException(field + " phải từ 1 đến " + max + " ký tự");
        }
        return clean;
    }
}
