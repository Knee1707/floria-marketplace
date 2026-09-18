package com.starshop.service.admin;

import com.starshop.domain.Delivery;
import java.math.BigDecimal;
import java.util.List;

/** HỢP ĐỒNG — TV3 viết DeliveryServiceImpl (@owner: TV3 | F6.7). */
public interface DeliveryService {
    List<Delivery> all();
    void save(Long id, String name, BigDecimal shippingFee);
    void delete(Long id);
}
