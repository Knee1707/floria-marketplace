package com.starshop.service.admin;

import com.starshop.domain.Style;
import com.starshop.domain.StyleValue;
import java.util.List;

/** HỢP ĐỒNG — TV3 viết StyleServiceImpl (@owner: TV3 | F6.3). */
public interface StyleService {
    List<Style> allStyles();
    void saveStyle(Long id, String name);
    List<StyleValue> valuesOf(Long styleId);
    void addValue(Long styleId, String value);
    void deleteValue(Long valueId);
}
