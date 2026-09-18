package com.starshop.service.admin;

import com.starshop.domain.Commission;
import java.math.BigDecimal;
import java.util.List;

/** HỢP ĐỒNG — TV3 viết CommissionServiceImpl (@owner: TV3 | F6.5). */
public interface CommissionService {
    List<Commission> all();
    void save(Long id, Long storeId, BigDecimal rate, String note);
    void delete(Long id);
}
